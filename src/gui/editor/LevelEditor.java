package gui.editor;

import game.level.reader.RatGameFile;
import game.tile.Tile;
import gui.editor.module.dependant.CustomEventDataMap;
import gui.editor.module.dependant.LevelEditorDragHandler;
import gui.editor.module.dependant.LevelEditorMouseHandler;
import gui.editor.module.grid.entityview.EntityViewModule;
import gui.editor.module.grid.tileview.TileViewModule;
import gui.editor.module.tab.TabModules;
import gui.editor.module.tile.TileDragDropModule;
import gui.game.dependant.tilemap.Coordinates;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import util.SceneUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 * @version 0.7
 * Copyright: N/A
 */
public class LevelEditor implements Initializable, AutoCloseable {

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
     * The number of rows the level editor has as an observable value.
     */
    private SimpleIntegerProperty rowProperty;

    /**
     * The number of columns the level editor has as an observable value.
     */
    private SimpleIntegerProperty colProperty;

    /**
     * The stage that the level editor is being displayed on.
     */
    private Stage displayStage;

    /**
     * The rat game file object that is being edited. All modifications
     * are/can be saved to this file.
     */
    private RatGameFile fileToEdit;

    /**
     * Module consisting of the game tiles.
     */
    private TileViewModule tileViewModule;

    /**
     * Module consisting of all the game entities.
     */
    private EntityViewModule entityViewModule;

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

    /**
     * Event handlers that will handle specifically mouse events that will
     * occur on the game Stack pane.
     */
    private final List<LevelEditorMouseHandler> mouseEventHandleMap
            = Collections.synchronizedList(new ArrayList<>());

    /**
     * Editor bulk execution service, mostly used by the grid display
     * services when they need to do a bulk update such as changing a lot of
     * tiles or loading a large number of sprites.
     * <p>
     * A fixed thread pool is used here over a Cached thread pool as we
     * cannot guarantee that the number of tasks submitted at any one time is
     * reasonable, i.e., TileViewModule can submit up to (v * k), min bounded
     * as (1 * 1); max bounded as (128 * 128) (which is 16384 sprite loading
     * tasks which with a cached thread pool could theoretically mean we have
     * 16000+ Threads running).
     */
    private final ExecutorService editorBulkTaskService
            = Executors.newFixedThreadPool(3);

    ///////////////////////////////////////////////////////////////////////////
    // Scene FXML attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Stack pane consisting of the game sprite objects.
     */
    @FXML
    private StackPane gameObjectEditorViewStackPane;

    /**
     * Scroll pane which contains the {@link #gameObjectEditorViewStackPane}
     * this ensures that sizes always fit nicely.
     */
    @FXML
    private ScrollPane gameViewScrollPane;

    /**
     * HBox held at the top of the scene consisting of the Tiles ready for
     * drag and drop.
     */
    @FXML
    private HBox tilesHBox;

    /**
     * HBox held at the bottom of the editor consisting of the controls used
     * by the editor.
     */
    @FXML
    private HBox editorCustomControlsHBox;

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
            editor.rowProperty = new SimpleIntegerProperty(
                    fileToEdit.getDefaultProperties().getRows()
            );
            editor.colProperty = new SimpleIntegerProperty(
                    fileToEdit.getDefaultProperties().getColumns()
            );
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
        this.entityViewModule = new EntityViewModule();

        Platform.runLater(() -> {
            tileDragDropModule.loadIntoScene(this);
            tileViewModule.loadIntoScene(this);
            tabModules.loadIntoScene(this);
            entityViewModule.loadIntoScene(this);
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

    /**
     * Event filter for mouse clicked events.
     *
     * @param e Mouse event.
     */
    @FXML
    private void onMouseClicked(final MouseEvent e) {
        handleMouseEvent(e, MouseEvent.MOUSE_CLICKED);
    }

    /**
     * Event filter for mouse dragged events.
     *
     * @param e Mouse event.
     */
    @FXML
    private void onMouseDragged(final MouseEvent e) {
        handleMouseEvent(e, MouseEvent.MOUSE_DRAGGED);
    }

    /**
     * Event filter for mouse entered events.
     *
     * @param e Mouse event.
     */
    @FXML
    private void onMouseEntered(final MouseEvent e) {
        handleMouseEvent(e, MouseEvent.MOUSE_ENTERED);
    }

    /**
     * Event filter for mouse released events.
     *
     * @param e Mouse event.
     */
    public void onMouseReleased(final MouseEvent e) {
        handleMouseEvent(e, MouseEvent.MOUSE_RELEASED);
    }

    /**
     * Event filter for mouse pressed events.
     *
     * @param e Mouse event.
     */
    public void onMousePressed(final MouseEvent e) {
        handleMouseEvent(e, MouseEvent.MOUSE_PRESSED);
    }

    /**
     * Mouse event filter method fires off all respective mouse event
     * handlers.
     *
     * @param e    The event to pass forward.
     * @param type The type of the mouse event that was triggered.
     */
    private void handleMouseEvent(final MouseEvent e,
                                  final EventType<MouseEvent> type) {
        final Coordinates<Integer> pos = getCoordinates(e);

        // Mouse X,Y can occur at negative coordinates
        if ((pos.getCol() >= 0)
                && (pos.getCol() < this.getCols())
                && (pos.getRow() >= 0)
                && (pos.getRow() < this.getRows())) {
            this.mouseEventHandleMap
                    .listIterator()
                    .forEachRemaining(i -> {
                        i.handle(
                                type,
                                e,
                                pos.getRow(),
                                pos.getCol());
                    });
        }
    }

    /**
     * Convenience method for calculating Row, Col positions for a mouse event.
     *
     * @param e The event to parse.
     * @return Row, Col positions from the mouse x, y.
     */
    private Coordinates<Integer> getCoordinates(final MouseEvent e) {
        return SceneUtil.getRowColFromPX(
                e.getX(),
                e.getY(),
                Tile.DEFAULT_SIZE
        );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Stage content zoom handlers; Could argue is duplicated code, but it
    // would probably be more confusing/obfuscated to separate.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Applies a positive scale factor to the game StackPane enlarging the
     * view.
     */
    @FXML
    private void onZoomIn() {
        final double zoomX = gameObjectEditorViewStackPane.getScaleX();
        final double zoomY = gameObjectEditorViewStackPane.getScaleY();
        final double increment = 0.05;

        this.gameObjectEditorViewStackPane.setScaleX(zoomX + increment);
        this.gameObjectEditorViewStackPane.setScaleY(zoomY + increment);
    }

    /**
     * Applies a negative scale factor to the game StackPane making the view
     * smaller.
     */
    @FXML
    private void onZoomOut() {
        final double zoomX = gameObjectEditorViewStackPane.getScaleX();
        final double zoomY = gameObjectEditorViewStackPane.getScaleY();
        final double increment = 0.05;

        this.gameObjectEditorViewStackPane.setScaleX(zoomX - increment);
        this.gameObjectEditorViewStackPane.setScaleY(zoomY - increment);
    }

    /**
     * Resets the StackPane scale to its default scale values.
     */
    @FXML
    private void onResetZoom() {
        final double initialValue = 1.0;
        this.gameObjectEditorViewStackPane.setScaleX(initialValue);
        this.gameObjectEditorViewStackPane.setScaleY(initialValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data collection/get methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return The number of rows the level editor has for its entity, and
     * tile map.
     */
    public int getRows() {
        return this.rowProperty.get();
    }

    /**
     * @return Gets the Row property object which consists of the number of
     * rows the game tile maps should have.
     */
    public SimpleIntegerProperty rowProperty() {
        return rowProperty;
    }

    /**
     * @return The number of columns the level editor has for its entity, and
     * tile map.
     */
    public int getCols() {
        return this.colProperty.get();
    }

    /**
     * @return Gets the col property object which consists of the number of
     * columns the game tile maps should have.
     */
    public SimpleIntegerProperty colProperty() {
        return colProperty;
    }

    /**
     * Sets the pannable state of the game scroll pane to the provided state.
     * Does so in a synchronised manner.
     *
     * @param isPannable The new pannable state for the game scroll pane.
     */
    public synchronized void setPannable(final boolean isPannable) {
        this.getGameScrollPane().setPannable(isPannable);
    }

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
     * @return The tab view container modules.
     */
    public TabModules getTabModules() {
        return tabModules;
    }

    /**
     * @return Game entity display scene which consists of all the games
     * entities.
     */
    public EntityViewModule getEntityViewModule() {
        return entityViewModule;
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

    /**
     * Adds a mouse event handler to the Grid StackPane. The target events
     * can be identified via constants like so {@link MouseEvent#DRAG_DETECTED}.
     *
     * @param handle The handle to call on mouse events.
     * @see MouseEvent#MOUSE_CLICKED
     * @see MouseEvent#MOUSE_DRAGGED
     * @see MouseEvent#MOUSE_ENTERED
     */
    public void addMouseEventHandle(final LevelEditorMouseHandler handle) {
        this.mouseEventHandleMap.add(handle);
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

    /**
     * @return HBox consisting of all the scenes custom control elements.
     */
    public HBox getEditorCustomControlsHBox() {
        return editorCustomControlsHBox;
    }

    /**
     * @return The scroll pane consisting of the game stackpane.
     */
    public ScrollPane getGameScrollPane() {
        return this.gameViewScrollPane;
    }

    /**
     * Queues the provided task for async execution. The task will run,
     * unless cancelled by the caller using the returned future.
     *
     * @param task The task to run.
     * @return The future representation of said task.
     */
    public Future<?> runLater(final Runnable task) {
        if (!this.editorBulkTaskService.isShutdown()) {
            return this.editorBulkTaskService.submit(task);

            // Error service unavailable.
        } else {
            final Class<?> caller = StackWalker.getInstance(
                    StackWalker.Option.RETAIN_CLASS_REFERENCE
            ).getCallerClass();
            throw new IllegalStateException(String.format(
                    "Task [%s] submitted by [%s] cannot be queued as the "
                            + "executor is currently shutdown.",
                    task.toString(),
                    caller.getName()
            ));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Auto closable interface element; this just simplifies the resource
    // management for level editor. As, long as you remember to wrap the
    // object with a try resource block.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     */
    @Override
    public void close() throws Exception {
        this.editorBulkTaskService.shutdown();
    }
}
