package gui.editor;

import game.generator.RatItemInventory;
import game.level.reader.RatGameFile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class LevelEditor implements Initializable {

    private static final URL SCENE_FXML
            = LevelEditor.class.getResource("LevelEditorMain.fxml");

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    private Parent root;
    private Stage displayStage;
    private RatGameFile fileToEdit;

    ///////////////////////////////////////////////////////////////////////////
    // Scene FXML attributes
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private BorderPane editorTileViewBorderpane;
    @FXML
    private HBox tilesHBox;
    @FXML
    private BorderPane generalTabBorderpane;
    @FXML
    private BorderPane entitiesTabBorderpane;
    @FXML
    private BorderPane itemPoolTabBorderpane;

    ///////////////////////////////////////////////////////////////////////////
    // Static constructors
    ///////////////////////////////////////////////////////////////////////////

    public static LevelEditor init(final Stage s,
                                   final RatGameFile fileToEdit) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final LevelEditor editor = loader.getController();

            editor.root = root;
            editor.displayStage = s;
            editor.fileToEdit = fileToEdit;

            s.setScene(new Scene(editor.root));

            return editor;

            // Stack trace + rethrow
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Scene gets loaded here; all aspects/editor modules
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {

    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handler methods
    ///////////////////////////////////////////////////////////////////////////

    public void onSaveAndQuit() {
        getDisplayStage().close();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data collection/get methods
    ///////////////////////////////////////////////////////////////////////////

    public Parent getRoot() {
        return root;
    }

    public Stage getDisplayStage() {
        return displayStage;
    }

    public RatGameFile getFileToEdit() {
        return fileToEdit;
    }
}
