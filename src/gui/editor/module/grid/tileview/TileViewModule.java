package gui.editor.module.grid.tileview;

import game.level.reader.module.GameProperties;
import game.tile.Tile;
import gui.editor.LevelEditor;
import gui.editor.init.LevelEditorBuilder;
import gui.editor.module.dependant.LevelEditorModule;
import gui.game.dependant.tilemap.GameMap;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import util.SceneUtil;

import java.lang.reflect.MalformedParametersException;
import java.util.function.Function;

/**
 * Java class created on 12/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class TileViewModule implements LevelEditorModule {

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
                    setInteractiveElement(fxView);

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
     * Sets an interactive element to the target node.
     *
     * @param n The node to attach the interactive elements on.
     */
    private void setInteractiveElement(final Node n) {
        // todo I managed to break this somehow
        n.setOnDragEntered((e) -> {
            System.out.println("Drag entered!");
        });
        n.setOnDragEntered((e) -> {
            System.out.println("Drag exited!");
        });
    }

    /**
     * Resizes this module, and dependant modules in accordance with this new
     * size.
     *
     * @param rows The new number of rows.
     * @param cols The new number of columns.
     * @throws MalformedParametersException If either the rows, or cols is
     *                                      malformed.
     */
    public void setSize(final int rows,
                        final int cols) {
        final Function<Integer, Boolean> fn = (i) -> {
            return (i > 0) && (i < LevelEditorBuilder.MAXIMUM_GRID_SIZE);
        };

        // Ensure proper size
        if (!fn.apply(rows) || !fn.apply(cols)) {
            throw new MalformedParametersException(String.format(
                    "Provided size: (%s, %s) is malformed!!!",
                    rows,
                    cols
            ));

            // Resize the view
        } else {
            // todo finish this when ready
        }
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

    public Tile[] getAdjacentTiles(final int row, final int col) {
        Tile[] tiles = new Tile[4];
        TileInfo currentTile = getTileInfoFor(row, col);

        if (currentTile.getNorth().isPresent()) {
            tiles[0] = currentTile.getNorth().get();
        } else {
            tiles[0] = null;
        }
        if (currentTile.getEast().isPresent()) {
            tiles[1] = currentTile.getEast().get();
        } else {
            tiles[1] = null;
        }
        if (currentTile.getSouth().isPresent()) {
            tiles[2] = currentTile.getSouth().get();
        } else {
            tiles[2] = null;
        }
        if (currentTile.getWest().isPresent()) {
            tiles[3] = currentTile.getWest().get();
        } else {
            tiles[3] = null;
        }

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
}
