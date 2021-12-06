package game.level;

import game.level.reader.RatGameFile;
import game.level.reader.exception.RatGameFileException;
import game.tile.Tile;
import game.tile.exception.UnknownSpriteEnumeration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Level acts as an intermediary object to wrap a generic Level of Tiles for
 * usage within the Rat Game.
 *
 * @author -Ry
 * @version 0.3
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
     * @param rowCount    The width of the map. The number of columns it has.
     * @param columnCount The columns of the map. The number of rows it has.
     * @param path    The absolute file path to the level file.
     */
    public Level(final int rowCount,
                 final int columnCount,
                 final String path) {
        this.rows = rowCount;
        this.columns = columnCount;
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

    /**
     * @return An optional of the enum ordinal that this level is represented
     * by if one could be deduced. However, if one could not be deduced than
     * an empty optional is returned.
     * @throws UnknownSpriteEnumeration If the data held within the default
     *                                  file is malformed.
     * @throws RatGameFileException     If whilst reading the level file an
     *                                  exception is thrown due to malformed
     *                                  data.
     * @throws IOException              If the file could not be read at all.
     */
    public RatGameFile getAsRatGameFile()
            throws UnknownSpriteEnumeration,
            RatGameFileException,
            IOException {
        return new RatGameFile(new File(this.levelFile));
    }
}
