package gui.editor.init.forms.filestructure;

import game.level.levels.template.TemplateEditor;
import game.level.reader.RatGameFile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import util.FileSystemUtil;
import util.SceneUtil;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2. Form
 * used to collect all the essential file structure information for the save
 * editor. Things like custom filename, existing file, new file, and so on.
 * This form however does not collect the Row, Col, or metadata information
 * of the subject target file.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class CustomFileStructureForm implements Initializable {

    ///////////////////////////////////////////////////////////////////////////
    // The idea of these forms is that we sequence them together in a Builder
    // style chain to collect all the required data for the Editor to
    // initialise.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = CustomFileStructureForm.class.getResource(
            "CustomFileStructureForm.fxml");

    /**
     * Static construction mechanism for initialising the class.
     *
     * @param s The stage to initialise into.
     * @return Newly loaded and setup instance.
     */
    public static CustomFileStructureForm init(final Stage s) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final CustomFileStructureForm form = loader.getController();
            form.root = root;
            form.displayStage = s;

            s.setScene(new Scene(root));

            return form;
            // Stacktrace + rethrow
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class relevant information
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Root Node of this scene.
     */
    private Parent root;

    /**
     * The stage that this scene is being displayed on.
     */
    private Stage displayStage;

    /**
     * Button used for loading up a .RGF file.
     */
    @FXML
    private MenuButton insertFileButton;

    /**
     * Radio button for loading from an existing level.
     */
    @FXML
    private RadioButton existingLevelRadio;

    /**
     * Radio button for loading a brand-new level.
     */
    @FXML
    private RadioButton newLevelRadio;

    /**
     * Text field which will be populated with the chosen files' path if an
     * existing level is chosen.
     */
    @FXML
    private TextField filePathTextField;

    /**
     * The literal file object instance to the selected file.
     */
    private File selectedFile;

    /**
     * If an existing file is chosen then a sanity check on the files quality
     * is done, in order to not waste resources this file is kept in memory
     * for later usage.
     */
    private RatGameFile loadedSelectedFile;

    /**
     * Text field which consists of the users custom level name.
     */
    @FXML
    private TextField customFilenameField;

    /**
     * Using the custom level name alongside
     * {@link TemplateEditor#CUSTOM_FILES_DIR} we can obtain this path. This
     * file may or may not already exist it doesn't matter here though.
     */
    private File customFileLocation;

    /**
     * Boolean check of natural exits of the scene. If this is false we
     * cannot viably say that the program is in a state that can load an
     * Editor for the target file.
     */
    private boolean isNaturalExit = false;

    ///////////////////////////////////////////////////////////////////////////
    // Class event based method handles
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Initialise method for loading and setting restrictions to the scene
     * information.
     *
     * @param url    Unused.
     * @param bundle Unused.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {
        // Force the filename field to use only safe chars
        this.customFilenameField.setTextFormatter(new TextFormatter<>((c) -> {
            if (c.getControlNewText().matches("[a-zA-Z0-9 _-]*")) {
                return c;
            } else {
                return null;
            }
        }));

        // Index the array backwards because its in ascending order
        final File[] files = FileSystemUtil.getLatestReadRgfFiles();
        for (int i = files.length - 1; i >= 0; --i) {
            final File f = files[i];
            final MenuItem item = new MenuItem();
            item.setText(f.getName());
            item.setUserData(f);

            item.setOnAction((eve) -> {
                final MenuItem o = (MenuItem) eve.getSource();
                this.sanityCheckSelectedFile((File) o.getUserData());
            });
            this.insertFileButton.getItems().add(item);
        }
    }

    /**
     * Updates the state of the File structure for existing files.
     */
    @FXML
    private void onExistingLevelSelected() {
        newLevelRadio.setSelected(false);
        existingLevelRadio.setSelected(true);
        insertFileButton.setDisable(false);
    }

    /**
     * Updates the state of the File structure for new files.
     */
    @FXML
    private void onNewLevelSelected() {
        existingLevelRadio.setSelected(false);
        newLevelRadio.setSelected(true);
        insertFileButton.setDisable(true);
    }

    /**
     * Loads up a File of the users choice. Does a sanity check if a file is
     * chosen to ensure that it is a good file to use.
     */
    private void onInsertFileClicked() {
        final FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        chooser.setTitle("Select a RatGameDefault File!");

        // This extension filter doesn't work on Windows 10; it's a known bug
        // that Oracle says they won't fix.
        chooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter(
                        "Rat Game Files (*.rgf)",
                        "*.rgf")
        );

        // This can be null, however we don't care right now
        this.selectedFile = chooser.showOpenDialog(new Popup());
        sanityCheckSelectedFile(this.selectedFile);
    }

    private void sanityCheckSelectedFile(final File f) {
        this.selectedFile = f;

        // Now we care
        if (this.selectedFile != null) {

            // Ensure it is a safe rat game file
            try {
                final RatGameFile file = new RatGameFile(this.selectedFile);

                this.loadedSelectedFile = file;
                this.customFilenameField.setText(
                        file.getDefaultProperties().getLevelName()
                );
                this.filePathTextField.setText(
                        this.selectedFile.getAbsolutePath()
                );

                // Case for a bad file
            } catch (final Exception e) {
                final Alert ae = new Alert(Alert.AlertType.WARNING);
                ae.setHeaderText("Broken File Selected!");
                ae.setContentText("The file selected did not pass the "
                        + "required sanity checks for a Rat Game File.");
                ae.showAndWait();
            }
        }
    }

    /**
     * Event handler for when the insert file button is clicked.
     *
     * @param e The mouse event that was fired.
     */
    @FXML
    private void onMouseClicked(final MouseEvent e) {
        if (SceneUtil.wasRightClick(e)) {
            this.onInsertFileClicked();
        }
    }

    /**
     * Natural exit for the scene, finalises the forms.
     */
    @FXML
    private void onContinueClicked() {

        if (getSaveLocationFilename().isEmpty()) {
            final Alert ae = new Alert(Alert.AlertType.ERROR);
            ae.setHeaderText("Custom filename is required!");
            ae.setContentText("The custom filename field is a requirement in "
                    + "order to proceed further.");
            ae.showAndWait();

        } else {
            this.customFileLocation = new File(
                    TemplateEditor.CUSTOM_FILES_DIR
                            + "/"
                            + getSaveLocationFilename().get()
                            + ".rgf"
            ).getAbsoluteFile();
            this.isNaturalExit = true;
            displayStage.close();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class data collection methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return Optional of the save location. (This field can be the empty
     * string thus if so an empty optional is returned)
     */
    private Optional<String> getSaveLocationFilename() {
        if (this.customFilenameField.getText().equals("")) {
            return Optional.empty();
        } else {
            return Optional.of(this.customFilenameField.getText());
        }
    }

    /**
     * @return Root node of this scene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return The file that the user selected if any.
     */
    public Optional<File> getSelectedFile() {
        if ((this.selectedFile == null)
                || this.newLevelRadio.isSelected()) {
            return Optional.empty();

        } else {
            return Optional.of(this.selectedFile);
        }
    }

    /**
     * @return If a file is chosen then this should also be present, but if
     * not then an empty optional is returned.
     */
    public Optional<RatGameFile> getLoadedExistingFile() {
        if ((this.loadedSelectedFile == null)
                || this.newLevelRadio.isSelected()) {
            return Optional.empty();

        } else {
            return Optional.of(this.loadedSelectedFile);
        }
    }

    /**
     * @return The save location in the custom files' directory.
     */
    public File getCustomFileLocation() {
        return customFileLocation;
    }

    /**
     * @return {@code true} if the file structure is based around a new file.
     */
    public boolean isNewFile() {
        return this.newLevelRadio.isSelected();
    }

    /**
     * @return {@code true} if the file structure is based around an existing
     * Rat Game File.
     */
    public boolean isExistingFile() {
        return this.existingLevelRadio.isSelected();
    }

    /**
     * @return {@return true} if the scene exited normally, this implies that
     * the data is safe to use.
     */
    public boolean isNaturalExit() {
        return isNaturalExit;
    }

    /**
     * @return Filename of the save location for the custom file. This does
     * not include the .rgf part.
     */
    public String getCustomFilename() {
        final String s = getCustomFileLocation().getName();
        return s.substring(0, s.lastIndexOf("."));
    }
}
