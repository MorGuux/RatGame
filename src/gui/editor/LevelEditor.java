package gui.editor;

import game.level.reader.RatGameFile;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class LevelEditor {

    private static final URL SCENE_FXML
            = LevelEditor.class.getResource("LevelEditorMain.fxml");

    private Parent root;
    private Stage displayStage;
    private RatGameFile fileToEdit;

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
    // Event handler methods
    ///////////////////////////////////////////////////////////////////////////

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
