package gui.editor.module.grid.tileview;

import game.tile.Tile;
import gui.editor.LevelEditor;
import gui.editor.module.dependant.LevelEditorModule;
import gui.editor.module.dependant.LevelEditorMouseHandler;
import gui.game.dependant.tilemap.GameMap;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import util.SceneUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java class created on 12/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class TileViewModule implements
        LevelEditorModule,
        LevelEditorMouseHandler {

    /**
     * Game tile map which contains all the visual display sprites for this
     * module.
     */
    private GameMap map;

    /**
     * The raw underlying game tile map.
     */
    private Tile[][] tileMapRaw;

    /**
     * Loads the module into the level editor scene. So that it can be
     * interacted with.
     * <p>
     * Note that you should store the provided {@link LevelEditor} locally in
     * your class then use that when interacting with something in the editor.
     *
     * @param editor The editor to load into.
     */
    @Override
    public void loadIntoScene(final LevelEditor editor) {

        this.tileMapRaw = editor.getFileToEdit().getLevel().getTiles();

        final int numRows = editor.getRows();
        final int numCols = editor.getCols();

        editor.rowProperty().addListener(this::sizeUpdate);
        editor.colProperty().addListener(this::sizeUpdate);
        editor.addMouseEventHandle(this);

        // Create the game map
        this.map = new GameMap(
                numRows,
                numCols,
                new GridPaneFactory.CenteredGridPane()
        );

        // Load the tiles into the game map
        for (int row = 0; row < numRows; ++row) {
            for (int col = 0; col < numCols; ++col) {

                // The run later just means that when loading the image
                // sprite we can do something else as well.
                final int r = row;
                final int c = col;
                editor.runLater(() -> {
                    final ImageView fxView
                            = tileMapRaw[r][c].getFXSpriteView();

                    // Scene modifications need to be done on the JavaFX
                    // thread.
                    Platform.runLater(() -> {
                        SceneUtil.scaleNodeIn(fxView);
                        SceneUtil.fadeInNode(fxView);
                        this.map.setNodeAt(
                                r,
                                c,
                                fxView
                        );
                    });
                });
            }
        }

        editor.getGameObjectEditorViewStackPane().getChildren().add(
                map.getRoot()
        );
    }

    private void sizeUpdate(final ObservableValue<?> obj,
                            final Number old,
                            final Number v) {
        // todo Size update action
    }

    /**
     * Gets the tile at the specified row, col.
     *
     * @param row The row index.
     * @param col The col index.
     * @return The tile held at that index.
     * @throws IndexOutOfBoundsException If the provided row, or col, is out
     *                                   of bounds.
     */
    public Tile getTileAt(final int row,
                          final int col) {
        return tileMapRaw[row][col];
    }

    /**
     * Gets the adjacent tiles around the provided row, col.
     *
     * @param row The centre row position.
     * @param col The centre col position.
     * @return All tiles in the cardinal directions around the centre point.
     * Using the format N,E,S,W.
     */
    public Tile[] getAdjacentTiles(final int row, final int col) {
        final int numDirections = 4;
        final Tile[] tiles = new Tile[numDirections];
        final TileInfo currentTile = getTileInfoFor(row, col);

        final AtomicInteger count = new AtomicInteger(0);
        currentTile.stream()
                .skip(1)
                .forEach(i -> tiles[count.getAndIncrement()] = i.orElse(null));

        return tiles;
    }

    /**
     * Creates a tile info object centered at the provided row, col.
     *
     * @param row The row position of the center tile.
     * @param col The col position of the center tile.
     * @return Newly constructed tile info centered around the provided row,
     * col.
     */
    public TileInfo getTileInfoFor(final int row,
                                   final int col) {
        return new TileInfo(row, col, this.tileMapRaw);
    }

    /**
     * @return The raw tile map array.
     */
    public Tile[][] getTileMapRaw() {
        return tileMapRaw;
    }

    /**
     * Updates this modules state in accordance with the provided tile.
     *
     * @param tile The tile to set in this view.
     * @throws IndexOutOfBoundsException If the provided Tile is specified at
     *                                   an invalid index.
     */
    public void setTile(final Tile tile) {
        final ImageView displayView = tile.getFXSpriteView();
        tileMapRaw[tile.getRow()][tile.getCol()] = tile;

        SceneUtil.fadeInNode(displayView);
        this.map.setNodeAt(tile.getRow(), tile.getCol(), displayView);
    }

    /**
     * Handles any and all mouse event types that can occur, such as Mouse
     * Clicked or Mouse Dragged.
     *
     * @param type The actual event type such as Drag, or Click.
     * @param e    The event data.
     * @param row  The corresponding Row position in the display grid.
     * @param col  The corresponding Col position in the display grid.
     */
    @Override
    public void handle(final EventType<MouseEvent> type,
                       final MouseEvent e,
                       final int row,
                       final int col) {
        if (type.equals(MouseEvent.MOUSE_CLICKED)
                && SceneUtil.wasLeftClick(e)) {
            SceneUtil.fadeInNode(this.map.getNodeAt(row, col));
        }
    }
}
