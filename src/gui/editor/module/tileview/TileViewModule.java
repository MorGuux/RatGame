package gui.editor.module.tileview;

import game.level.reader.module.GameProperties;
import game.tile.Tile;
import gui.editor.LevelEditor;
import gui.editor.init.LevelEditorBuilder;
import gui.editor.module.dependant.LevelEditorModule;
import gui.game.dependant.tilemap.GameMap;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

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
     * The number of rows of the game map.
     */
    private int numRows;

    /**
     * The number of columns in the game map.
     */
    private int numCols;

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
        final GameProperties p
                = editor.getFileToEdit().getDefaultProperties();
        this.numRows = p.getRows();
        this.numCols = p.getColumns();
        this.tileMapRaw = editor.getFileToEdit().getLevel().getTiles();

        // Create the game map
        this.map = new GameMap(
                numRows,
                numCols,
                new GridPaneFactory.CenteredGridPane()
        );

        // Load the tiles into the game map
        for (int row = 0; row < numRows; ++row) {
            for (int col = 0; col < numCols; ++col) {

                final ImageView fxView = tileMapRaw[row][col].getFXSpriteView();
                setInteractiveElement(fxView);

                this.map.setNodeAt(
                        row,
                        col,
                        fxView
                );
            }
        }

        editor.getGameObjectEditorViewStackPane().getChildren().add(
                map.getRoot()
        );
    }

    /**
     * Applies interactive effects to the provided view.
     *
     * @param view The view to apply said effects to.
     */
    private void setInteractiveElement(final ImageView view) {

        final FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setFromValue(1.0);
        transition.setToValue(0.5);
        transition.setCycleCount(Transition.INDEFINITE);
        transition.setAutoReverse(true);
        transition.setNode(view);

        view.setOnDragEntered((e) -> {
            transition.playFromStart();
        });

        view.setOnDragExited((e) -> {
            transition.stop();
            view.setOpacity(1.0);
        });
    }

    /**
     * @return The number of rows in the tile view module.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * @return The number of columns in the tile view module.
     */
    public int getNumCols() {
        return numCols;
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
        setInteractiveElement(displayView);

        tileMapRaw[tile.getRow()][tile.getCol()] = tile;
        this.map.setNodeAt(tile.getRow(), tile.getCol(), displayView);
    }
}
