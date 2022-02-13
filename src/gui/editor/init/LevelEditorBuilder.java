package gui.editor.init;

import game.level.levels.template.TemplateEditor;
import game.level.reader.RatGameFile;
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
 * Java class created on 11/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class LevelEditorBuilder {

    private static final int MAXIMUM_GRID_SIZE = 512;

    private final CustomFileStructureForm fileStructureForm;
    private final NewFileSetupForm newFileSetupForm;
    private final Stage displayStage;

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

    private void sanityCheckFields() {

        // Forms need to terminate properly
        if (!fileStructureForm.isNaturalExit()
                || (newFileSetupForm != null
                && !newFileSetupForm.isNaturalExit())) {
            throw new IllegalStateException("Forms didnt terminate naturally!");
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

    private void loadRequiredDataToLocation(final File f) throws IOException {

        if (fileStructureForm.isNewFile()) {

            final TemplateEditor editor = newFileSetupForm.createEditor();
            editor.writeContentToFile(f);

            // Existing file (Copy whole file into new file)
        } else if (fileStructureForm.getLoadedExistingFile().isPresent()) {
            Files.writeString(
                    f.toPath(),
                    fileStructureForm.getLoadedExistingFile()
                            .get()
                            .getContent()
            );
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Standard data collection methods
    ///////////////////////////////////////////////////////////////////////////

    public Stage getDisplayStage() {
        return displayStage;
    }

    public CustomFileStructureForm getCustomFileStructureForm() {
        return fileStructureForm;
    }

    public Optional<NewFileSetupForm> getNewFileSetupForm() {
        if (newFileSetupForm == null) {
            return Optional.empty();
        } else {
            return Optional.of(newFileSetupForm);
        }
    }

    public File getCustomFile() {
        return this.fileStructureForm.getCustomFileLocation();
    }

    public LevelEditor build() throws Exception {
        return LevelEditor.init(
                getDisplayStage(),
                new RatGameFile(getCustomFile())
        );
    }
}
