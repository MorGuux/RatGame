package gui.editor.init;

import game.level.levels.template.TemplateEditor;
import game.level.levels.template.TemplateElement;
import game.level.reader.RatGameFile;
import game.level.writer.RatGameFileWriter;
import gui.editor.LevelEditor;
import gui.editor.init.forms.filestructure.CustomFileStructureForm;
import gui.editor.init.forms.setup.NewFileSetupForm;
import javafx.stage.Stage;
import util.SceneUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.MalformedParametersException;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2. Class
 * simplifies the File structure loading operations required to initialise
 * the Level builder.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public class LevelEditorBuilder {

    /**
     * The maximum size of the Tile grid array/game map.
     */
    public static final int MAXIMUM_GRID_SIZE = 128;

    /**
     * Form used to initialise the base structure.
     */
    private final CustomFileStructureForm fileStructureForm;

    /**
     * Form specifically used when loading a brand-new level.
     */
    private final NewFileSetupForm newFileSetupForm;

    /**
     * The stage that the editor builder will use when loading.
     */
    private final Stage displayStage;

    /**
     * @param s The stage to utilise when gathering dependencies.
     * @throws Exception If any occur whilst attempting to create the builder.
     */
    public LevelEditorBuilder(final Stage s) throws Exception {
        this.displayStage = s;

        fileStructureForm = CustomFileStructureForm.init(displayStage);
        SceneUtil.fadeInNode(fileStructureForm.getRoot());
        s.showAndWait();

        // If it is a new File
        if (fileStructureForm.isNewFile()) {
            this.newFileSetupForm = NewFileSetupForm.init(displayStage);
            SceneUtil.fadeInNode(newFileSetupForm.getRoot());
            s.showAndWait();
        } else {
            this.newFileSetupForm = null;
        }

        // Ensure correct setup data (Exceptions are thrown here)
        sanityCheckFields();


        // Setting up target file destination
        final File customLocation = getCustomFile();

        // The file must exist or be created
        if (customLocation.isFile()
                || customLocation.createNewFile()) {
            loadRequiredDataToLocation(customLocation);
        } else {
            throw new IOException("Custom file location failed to create!");
        }
    }

    /**
     * Checks to see if the required data held within the created forms is
     * correct and not malformed.
     *
     * @throws IllegalStateException        If the forms didn't terminate
     *                                      naturally.
     * @throws MalformedParametersException If data held within the forms is
     *                                      malformed.
     */
    private void sanityCheckFields() {

        // Forms need to terminate properly
        if (!fileStructureForm.isNaturalExit()
                || (newFileSetupForm != null
                && !newFileSetupForm.isNaturalExit())) {
            throw new IllegalStateException(
                    "Forms didnt terminate naturally!"
            );
        }

        // Sanity check the fields held within
        if (newFileSetupForm != null) {

            assert newFileSetupForm.getRowCount().isPresent()
                    && newFileSetupForm.getColumnCount().isPresent();

            int rows = newFileSetupForm.getRowCount().get();
            int cols = newFileSetupForm.getColumnCount().get();

            if (rows > MAXIMUM_GRID_SIZE || cols > MAXIMUM_GRID_SIZE) {
                throw new MalformedParametersException(String.format(
                        "Provided Size: (%s, %s) is invalid; maximum is: "
                                + "(%s, %s)",
                        rows,
                        cols,
                        MAXIMUM_GRID_SIZE,
                        MAXIMUM_GRID_SIZE
                ));
            }
        }
    }

    /**
     * Loads into the provided file the required content. This is so to make
     * it compatible with when loading with {@link RatGameFile}.
     *
     * @param f The file to make compatible.
     * @throws IOException If any occur whilst attempting to read/write to
     *                     the target file.
     */
    private void loadRequiredDataToLocation(final File f) throws IOException {

        if (fileStructureForm.isNewFile()) {

            final TemplateEditor editor = newFileSetupForm.createEditor();
            editor.writeContentToFile(f);

            // Existing file (Copy whole file into new file)
        } else if (fileStructureForm.getLoadedExistingFile().isPresent()) {

            final RatGameFileWriter writer = new RatGameFileWriter(
                    fileStructureForm.getLoadedExistingFile().get()
            );
            writer.writeElement(
                    TemplateElement.LEVEL_ID_NAME,
                    TemplateEditor.CUSTOM_LEVEL_ID
            );

            Files.writeString(
                    f.toPath(),
                    writer.getModifiedContent()
            );
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Standard data collection methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return The stage that the builder is utilising for form data.
     */
    public Stage getDisplayStage() {
        return displayStage;
    }

    /**
     * @return The custom file structure form.
     */
    public CustomFileStructureForm getCustomFileStructureForm() {
        return fileStructureForm;
    }

    /**
     * @return Empty optional if the builder is building from an existing
     * file. Else, an optional consisting of the new file setup form.
     */
    public Optional<NewFileSetupForm> getNewFileSetupForm() {
        if (newFileSetupForm == null) {
            return Optional.empty();
        } else {
            return Optional.of(newFileSetupForm);
        }
    }

    /**
     * @return Custom rat game file object.
     */
    public File getCustomFile() {
        return this.fileStructureForm.getCustomFileLocation();
    }

    /**
     * Loads using the gathered data a Level editor object.
     *
     * @return Newly constructed instance of the level editor.
     * @throws Exception If any occur whilst attempting to build the editor.
     */
    public LevelEditor build() throws Exception {
        return LevelEditor.init(
                getDisplayStage(),
                new RatGameFile(getCustomFile())
        );
    }
}
