package gui.game.dependant.tilemap;

import java.util.Objects;

/**
 * Simple X, Y number Coordinate Class that wraps coordinates of any Number
 * type.
 *
 * @param <T> The number type of the coordinates. Such as {@link Double} or
 * {@link Integer}.
 */
public class Coordinates<T extends Number> {

    /**
     * Current X value of this Coordinate.
     */
    private T x;

    /**
     * Current Y value of this Coordinate.
     */
    private T y;

    /**
     * Constructs the co-ordinates from the provided starting values.
     *
     * @param initX Initial X coordinate.
     * @param initY Initial Y coordinate.
     * @throws NullPointerException If either parameters are {@code null}.
     */
    public Coordinates(final T initX,
                       final T initY) {
        Objects.requireNonNull(initX);
        Objects.requireNonNull(initY);
        this.x = initX;
        this.y = initY;
    }

    /**
     * @return X value of this coordinate.
     */
    public T getX() {
        return x;
    }

    /**
     * Set the X value of this coordinate.
     *
     * @param newX New X value.
     */
    public void setX(final T newX) {
        this.x = newX;
    }

    /**
     * @return Y value of this coordinate.
     */
    public T getY() {
        return y;
    }

    /**
     * Set the Y value of this coordinate.
     *
     * @param newY The new Y value.
     */
    public void setY(final T newY) {
        this.y = newY;
    }
}
