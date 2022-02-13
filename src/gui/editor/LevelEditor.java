package gui.editor;

import game.level.reader.RatGameFile;
import game.tile.Tile;
import gui.editor.module.dependant.LevelEditorDragHandler;
import gui.editor.module.dependant.CustomEventDataMap;
import gui.editor.module.tab.TabModules;
import gui.editor.module.tile.TileDragDropModule;
import gui.editor.module.tileview.TileViewModule;
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
import javafx.scene.layout.StackPane;
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

    /**
     * Scene resources fxml.
     */
    private static final URL SCENE_FXML
            = LevelEditor.class.getResource("LevelEditorMain.fxml");

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Root node of the level editor scene.
     */
    private Parent root;

    /**
     * The stage that the level editor is being displayed on.
     */
    private Stage displayStage;

    /**
     * The rat game file object that is being edited. All modifications are
     * saved to this file.
     */
    private RatGameFile fileToEdit;

    /**
     * Module consisting of the game tiles
     */
    private TileViewModule tileViewModule;

    /**
     * Module consisting of all the possible drag and droppable tiles.
     */
    private TileDragDropModule tileDragDropModule;

    /**
     * Module consisting of all the tab data.
     */
    private TabModules tabModules;

    /**
     * Event redirection map so that we don't have to handle the events
     * directly in this class.
     */
    private final Map<String, LevelEditorDragHandler> eventHandleMap
            = Collections.synchronizedMap(new HashMap<>());

    ///////////////////////////////////////////////////////////////////////////
    // Scene FXML attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Borderpane consisting of the tile view.
     */
    @FXML
    private StackPane gameObjectEditorViewStackPane;

    /**
     * HBox held at the top of the scene consisting of the Tiles ready for
     * drag and drop.
     */
    @FXML
    private HBox tilesHBox;

    /**
     * Tab which contains the general information about the target level.
     */
    @FXML
    private BorderPane generalTabBorderpane;

    /**
     * Tab containing the Entities which are drag droppable.
     */
    @FXML
    private BorderPane entitiesTabBorderpane;

    /**
     * Tab consisting of the Item generators for the level.
     */
    @FXML
    private BorderPane itemPoolTabBorderpane;

    ///////////////////////////////////////////////////////////////////////////
    // Static constructors
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Static constructor for initialising this scene.
     *
     * @param s          The stage to display onto.
     * @param fileToEdit The file to edit.
     * @return Newly constructed and setup instance.
     */
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

    /**
     * Loads in all the modules for the editor.
     *
     * @param url    Un-used.
     * @param bundle Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {
        //
        // LOAD ORDER IS IMPORTANT HERE, WE SPECIFICALLY WANT THE TILES
        // LOADED BEFORE THE ENTITIES, THIS IS SO THAT THE TILES ARE AT THE
        // BOTTOM OF THE STACK PANE, BEHIND THE ENTITIES.
        //

        this.tileDragDropModule = new TileDragDropModule();
        this.tileViewModule = new TileViewModule();
        this.tabModules = new TabModules();

        Platform.runLater(() -> {
            tileDragDropModule.loadIntoScene(this);
            tileViewModule.loadIntoScene(this);
            tabModules.loadIntoScene(this);
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handler methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Natural exit for the editor.
     */
    @FXML
    private void onSaveAndQuit() {
        getDisplayStage().close();
    }

    /**
     * Drag drop finalise method. This redirects the event to which
     * handler registered to handle it. If no handler is specified then a
     * message is printed to the Error stream.
     *
     * @param dragEvent The drag event to redirect.
     */
    @FXML
    private void onDragDropped(final DragEvent dragEvent) {
        dragEvent.consume();

        final double x = dragEvent.getX();
        final double y = dragEvent.getY();

        final Dragboard db = dragEvent.getDragboard();
        final String contentID
                = (String) db.getContent(CustomEventDataMap.CONTENT_ID);
        if (this.eventHandleMap.containsKey(contentID)) {

            final int row = (int) Math.floor(y / Tile.DEFAULT_SIZE);
            final int col = (int) Math.floor(x / Tile.DEFAULT_SIZE);

            this.eventHandleMap.get(
                    contentID
            ).handle(this, dragEvent, row, col);

            // Un-routed event
        } else {
            System.err.println("[UN-ROUTED-EVENT] :: " + contentID);
        }
    }

    /**
     * Intermediary drag operation for accepting a drag operation.
     *
     * @param dragEvent The drag event to accept.
     */
    @FXML
    private void onDragEntered(final DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
        dragEvent.consume();
    }

    /**
     * Intermediary drag operation for accepting a drag operation.
     *
     * @param dragEvent The drag event to accept.
     */
    @FXML
    private void onDragOver(final DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
        dragEvent.consume();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data collection/get methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return Root node for this scene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return The stage that this scene is currently being displayed in.
     */
    public Stage getDisplayStage() {
        return displayStage;
    }

    /**
     * @return The rat game file object that this editor is editing.
     */
    public RatGameFile getFileToEdit() {
        return fileToEdit;
    }

    /**
     * @return The tile drag drop module which consists of all the tiles
     * possible for drag dropping.
     */
    public TileDragDropModule getTileDragDropModule() {
        return tileDragDropModule;
    }

    /**
     * @return Game display scene which contains all the game tiles.
     */
    public TileViewModule getTileViewModule() {
        return tileViewModule;
    }

    /**
     * Registers for the target event name the handler which will handle said
     * event.
     *
     * @param eventName The event to handle.
     * @param handle    The handler that will handle the event.
     */
    public void addEventHandle(final String eventName,
                               final LevelEditorDragHandler handle) {
        this.eventHandleMap.put(eventName, handle);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Scene container node access
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return Scene container node.
     */
    public StackPane getGameObjectEditorViewStackPane() {
        return gameObjectEditorViewStackPane;
    }

    /**
     * @return Scene container node.
     */
    public BorderPane getEntitiesTabBorderpane() {
        return entitiesTabBorderpane;
    }

    /**
     * @return Scene container node.
     */
    public BorderPane getGeneralTabBorderpane() {
        return generalTabBorderpane;
    }

    /**
     * @return Scene container node.
     */
    public BorderPane getItemPoolTabBorderpane() {
        return itemPoolTabBorderpane;
    }

    /**
     * @return Scene container node.
     */
    public HBox getTilesHBox() {
        return tilesHBox;
    }
}
