package gui.editor.init.forms.filestructure;

import game.level.levels.template.TemplateEditor;
import game.level.reader.RatGameFile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

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

    private static final URL SCENE_FXML
            = CustomFileStructureForm.class.getResource(
            "CustomFileStructureForm.fxml");

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

    private Parent root;

    private Stage displayStage;

    @FXML
    private Button insertFileButton;

    @FXML
    private RadioButton existingLevelRadio;

    @FXML
    private RadioButton newLevelRadio;

    @FXML
    private TextField filePathTextField;

    private File selectedFile;

    private RatGameFile loadedSelectedFile;

    @FXML
    private TextField customFilenameField;

    private File customFileLocation;

    ///////////////////////////////////////////////////////////////////////////
    // Class event based method handles
    ///////////////////////////////////////////////////////////////////////////

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
    }

    @FXML
    private void onExistingLevelSelected() {
        newLevelRadio.setSelected(false);
        insertFileButton.setDisable(false);
    }

    @FXML
    private void onNewLevelSelected() {
        existingLevelRadio.setSelected(false);
        insertFileButton.setDisable(true);
    }

    @FXML
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

        // Now we care
        if (this.selectedFile != null) {

            // Ensure it is a safe rat game file
            try {
                final RatGameFile file = new RatGameFile(this.selectedFile);

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

    @FXML
    private void onContinueClicked() {

        if (getSaveLocationFilename().isEmpty()) {
            Alert ae = new Alert(Alert.AlertType.ERROR);
            ae.setHeaderText("Custom filename is required!");
            ae.setContentText("The custom filename field is a requirement in "
                    + "order to proceed further.");
            ae.showAndWait();

        } else {
            this.customFileLocation = new File(
                    TemplateEditor.CUSTOM_FILES_DIR
                    + "/"
                    + getSaveLocationFilename().get()
            ).getAbsoluteFile();
            displayStage.close();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class data collection methods
    ///////////////////////////////////////////////////////////////////////////

    private Optional<String> getSaveLocationFilename() {
        if (this.customFilenameField.getText().equals("")) {
            return Optional.empty();
        } else {
            return Optional.of(this.customFilenameField.getText());
        }
    }

    public Parent getRoot() {
        return root;
    }

    public Optional<File> getSelectedFile() {
        if ((this.selectedFile == null)
                || this.newLevelRadio.isSelected()) {
            return Optional.empty();

        } else {
            return Optional.of(this.selectedFile);
        }
    }

    public Optional<RatGameFile> getLoadedExistingFile() {
        if ((this.loadedSelectedFile == null)
                || this.newLevelRadio.isSelected()) {
            return Optional.empty();

        } else {
            return Optional.of(this.loadedSelectedFile);
        }
    }

    public File getCustomFileLocation() {
        return customFileLocation;
    }

    public boolean isNewFile() {
        return this.newLevelRadio.isSelected();
    }

    public boolean isExistingFile() {
        return this.existingLevelRadio.isSelected();
    }
}
