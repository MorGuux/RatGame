package game.contextmap;

import gui.game.dependant.tilemap.Coordinates;

/**
 * Cardinal Direction enumerations where the base direction is that of a 2D
 * Array indexing from zero.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public enum CardinalDirection {

    /**
     * Represents North in a 2D Array.
     */
    NORTH(-1, 0),

    /**
     * Represents East in a 2D Array.
     */
    EAST(0, 1),

    /**
     * Represents South in a 2D Array.
     */
    SOUTH(1, 0),

    /**
     * Represents West in a 2D Array.
     */
    WEST(0, -1);

    /**
     * Row change required.
     */
    private final int rowIncrement;

    /**
     * Col change required.
     */
    private final int colIncrement;

    /**
     * Instantiates a cardinal direction from the base row, col increments.
     * <p>
     * All values are relevant to a 2D Array indexing from Zero.
     *
     * @param row Row change needed to go in this direction.
     * @param col Col change needed to go in this direction.
     */
    CardinalDirection(final int row,
                      final int col) {
        this.rowIncrement = row;
        this.colIncrement = col;
    }

    /**
     * Creates a new Coordinates object based on the direction of travel for
     * this cardinal. Where the base is the provided parameters and the
     * direction is the change required to deviate in 'this' direction.
     * <p>
     * Note the coordinates are set up as: X = Row, Y = Col.
     *
     * @param curRow Initial row value.
     * @param curCol Initial col value.
     * @return Resulting position from traversal.
     */
    public Coordinates<Integer> traverse(final int curRow,
                                         final int curCol) {
        return new Coordinates<>(
                curRow + rowIncrement,
                curCol + colIncrement
        );
    }

    public static CardinalDirection getTravelDirection(final int rowStart,
                                                       final int colStart,
                                                       final int rowEnd,
                                                       final int colEnd) {

        if (rowStart == rowEnd) {
            if (colStart > colEnd) {
                return EAST;
            } else {
                return WEST;
            }
        }

        if (colStart == colEnd) {
            if (rowStart > rowEnd) {
                return SOUTH;
            } else {
                return NORTH;
            }
        }

        throw new IllegalArgumentException(String.format(
                "ERROR: FROM_(%s, %s)_TO_(%s, %s)%n",
                rowStart,
                colStart,
                rowEnd,
                colEnd
        ));
    }
}
