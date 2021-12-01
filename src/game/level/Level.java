package game.level;

import game.contextmap.ContextualMap;
import game.entity.Entity;
import game.tile.Tile;
import gui.game.dependant.tilemap.Coordinates;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Level acts as an intermediary object to wrap a generic Level of Tiles for
 * usage within the Rat Game.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class Level {

    /**
     * All the tiles that make up the level.
     */
    private final Tile[][] tiles;

    /**
     * Width of the map.
     */
    private final int rows;

    /**
     * Height of the map.
     */
    private final int columns;

    /**
     * Absolute path to the level file that this is target of.
     */
    private final String levelFile;

    /**
     * Construct a level file with no tile information.
     *
     * @param rows    The width of the map. The number of columns it has.
     * @param columns The columns of the map. The number of rows it has.
     * @param path    The absolute file path to the level file.
     */
    public Level(final int rows,
                 final int columns,
                 final String path) {
        this.rows = rows;
        this.columns = columns;
        this.tiles = new Tile[rows][columns];
        this.levelFile = path;
    }

    /**
     * Set a tile in the level to the provided tile.
     *
     * @param t   The tile to use/set.
     * @param row The row position of the tile.
     * @param col The column position of the tile.
     * @throws NullPointerException      If the provided Tile is null.
     * @throws IndexOutOfBoundsException If the row or column are out of
     *                                   bounds for the map size.
     * @implNote Indexes from zero for row, col.
     */
    public void setTile(final Tile t,
                        final int row,
                        final int col) {
        Objects.requireNonNull(t);
        this.tiles[row][col] = t;
    }

    /**
     * @param row Position to check for index constraints.
     * @param col Position to check for index constraints.
     * @return {@code true} if the provided Row, Col are both valid positions
     * in the game map.
     */
    public boolean isInbounds(final int row,
                              final int col) {
        return (row < columns) && (col < rows);
    }

    /**
     * Checks to see whether the constructed level is in a complete state for
     * the size constraints applied. Where it is complete iff it has no
     * {@code null} tiles.
     *
     * @return {@code true} if this map is in a complete state. Otherwise, if
     * not then {@code false} is returned.
     */
    public boolean isCompleteMap() {
        // Check for a null tile
        for (final Tile[] row : tiles) {
            for (final Tile tile : row) {
                if (tile == null) {
                    return false;
                }
            }
        }
        // Default case
        return true;
    }

    /**
     * @return The tile map for this level.
     */
    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * @return The number of rows the Level has.
     */
    public int getRows() {
        return rows;
    }

    /**
     * @return The number of columns the level has.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @return The absolute path of the level file.
     */
    public String getLevelFile() {
        return levelFile;
    }
}
