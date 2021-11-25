package gui.game.dependant.tilemap;

import java.util.Objects;

/**
 * Simple X, Y number Coordinate Class that wraps coordinates of any Number
 * type.
 *
 * @param <T> The number type of the coordinates. Such as {@link Double} or
 *            {@link Integer}.
 */
public class Coordinates<T extends Number> {

    /**
     * Current row of this Coordinate.
     */
    private T row;

    /**
     * Current column of this Coordinate.
     */
    private T col;

    /**
     * Constructs the co-ordinates from the provided starting values.
     *
     * @param initRow Initial row coordinate.
     * @param initCol Initial col coordinate.
     * @throws NullPointerException If either parameters are {@code null}.
     */
    public Coordinates(final T initRow,
                       final T initCol) {
        Objects.requireNonNull(initRow);
        Objects.requireNonNull(initCol);
        this.row = initRow;
        this.col = initCol;
    }

    /**
     * @return Row of this coordinate.
     */
    public T getRow() {
        return row;
    }

    /**
     * Set the row of this coordinate.
     *
     * @param newRow New row value.
     */
    public void setRow(final T newRow) {
        this.row = newRow;
    }

    /**
     * @return Column of this coordinate.
     */
    public T getCol() {
        return col;
    }

    /**
     * Set the Y value of this coordinate.
     *
     * @param newCol New column value.
     */
    public void setCol(final T newCol) {
        this.col = newCol;
    }

    /**
     * Determines if this Object is equal to another Object or not. Where
     * equality is based upon:
     * <ol>
     *     <li>Obj != null</li>
     *     <li>Obj is of Coordinates Class</li>
     *     <li>Obj x,y == this x,y</li>
     * </ol>
     */
    @Override
    public boolean equals(final Object obj) {
        // Defer nullity
        if (obj == null) {
            return false;
        }

        // Ensure correct class
        if (!(obj instanceof final Coordinates<?> cObj)) {
            return false;

        } else {
            // Check if values are equal
            return cObj.getRow().equals(this.getRow())
                    && cObj.getCol().equals(this.getCol());
        }
    }

    /**
     * @return Hashcode of this object based on its X and Y value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getCol());
    }
}
