package gui.editor;

import game.level.reader.RatGameFile;
import gui.editor.module.LevelEditorDragHandler;
import gui.editor.module.dependant.CustomEventDataMap;
import gui.editor.module.tile.TileDragDropModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

    private final Map<String, LevelEditorDragHandler> eventHandleMap
            = Collections.synchronizedMap(new HashMap<>());

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
        final TileDragDropModule tileModule = new TileDragDropModule();
        Platform.runLater(() -> {
            tileModule.loadIntoScene(this);
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handler methods
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private void onSaveAndQuit() {
        getDisplayStage().close();
    }

    @FXML
    private void onDragDropped(final DragEvent dragEvent) {
        dragEvent.consume();

        final Dragboard db = dragEvent.getDragboard();
        final String content
                = (String) db.getContent(CustomEventDataMap.CONTENT_ID);
        if (this.eventHandleMap.containsKey(content)) {
            this.eventHandleMap.get(
                    content
            ).handle(this, dragEvent);

            // Un-routed event
        } else {
            System.err.println("[UN-ROUTED-EVENT] :: " + dragEvent);
        }
    }

    @FXML
    private void onDragEntered(final DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
        dragEvent.consume();
    }

    @FXML
    private void onDragOver(final DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
        dragEvent.consume();
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

    public BorderPane getEditorTileViewBorderpane() {
        return editorTileViewBorderpane;
    }

    public BorderPane getEntitiesTabBorderpane() {
        return entitiesTabBorderpane;
    }

    public BorderPane getGeneralTabBorderpane() {
        return generalTabBorderpane;
    }

    public BorderPane getItemPoolTabBorderpane() {
        return itemPoolTabBorderpane;
    }

    public HBox getTilesHBox() {
        return tilesHBox;
    }

    public void addEventHandle(final String eventName,
                               final LevelEditorDragHandler handle) {
        this.eventHandleMap.put(eventName, handle);
    }
}
