package gui.entitymap;

import javafx.scene.Node;

public class GridMapper<T extends Node> {
    private final T object;
    private final int gridSize;
    private final int nodeSize;
    private int curX;
    private int curY;

    public GridMapper(final T obj,
                      final int gridSize,
                      final int nodeSize,
                      final int initX,
                      final int initY) {
        this.object = obj;
        this.gridSize = gridSize;
        this.nodeSize = nodeSize;
        this.curX = initX;
        this.curY = initY;
        obj.setLayoutX(getCenter(initX));
        obj.setLayoutY(getCenter(initY));
    }

    public T getObject() {
        return object;
    }

    public int getCurX() {
        return curX;
    }

    public int getCurY() {
        return curY;
    }

    public void setPosition(final int x,
                            final int y) {
        if (isValidPosition(x, y)) {
            if (x == curX) {
                translateY(y);
            } else {
                translateX(x);
            }
        } else {
            throw new IllegalStateException("Invalid Position: " + x + ", " + y);
        }
    }

    //todo find a way to slowly increment
    public void translateX(final int newX) {
        final int inc = getIncrement(getCurX(), newX);
        final int endVal = getCenter(newX);

        while (object.getLayoutX() != endVal) {
            object.setLayoutX(object.getLayoutX() + inc);
        }
        this.curX = newX;
    }

    //todo find a way to slowly increment
    public void translateY(final int newY) {
        final int inc = getIncrement(getCurY(), newY);
        final int endVal = getCenter(newY);

        while (object.getLayoutY() != endVal) {
            object.setLayoutY(object.getLayoutY() + inc);
        }
        this.curY = newY;
    }

    public boolean isValidPosition(final int x,
                                   final int y) {
        final int allowedBound = 1;
        final boolean boundedX = isWithinValue(curX, x, allowedBound);
        final boolean boundedY = isWithinValue(curY, y, allowedBound);
        if (boundedX && boundedY) {
            // Only allows bound changes of 1; in value,
            //  and number of changes
            if ((curY == y) && (curX != x)) {
                return true;

            } else {
                return (curX == x) && (curY != y);
            }

        } else {
            return false;
        }
    }

    /**
     * Checks to see if the provided base value, is:
     * <ol>
     *     <li>Equal to 'compare'</li>
     *     <li>'Compare' is greater than 'base' by the inc</li>
     *     <li>'Compare' is less than 'base' by the bound</li>
     * </ol>
     *
     * @param base    Base value to compare against.
     * @param compare Compare value to test bound.
     * @param inc     Value bound to test within.
     * @return {@code true} if compare is within base by the provided bound.
     * Otherwise, {@code false}.
     */
    private boolean isWithinValue(final int base,
                                  final int compare,
                                  final int inc) {
        return (base == compare)
                || (base == (compare + inc))
                || (base == (compare - inc));
    }

    /**
     * Calculates the pixel position of where 'this' sprite should be displayed
     * visually to be centered on a tile.
     *
     * @param val Cartesian X or Y value to map. (0,1) where 0, or 1 would be fine.
     * @return Pixel position offset by the size of the node to center the sprite.
     * @implNote Only guarantees that it will be centered if the Node is Square.
     */
    private int getCenter(final int val) {
        return (gridSize * val) + (nodeSize / 4);
    }

    /**
     * Deduces if we're going backwards or forwards in a Cardinal.
     *
     * @param val    Initial position.
     * @param newVal New position.
     * @return The value to increment 'val' by in order to eventually
     * get to 'newVal'
     */
    private int getIncrement(final int val,
                             final int newVal) {
        if (val < newVal) {
            return 1;
        } else {
            return -1;
        }
    }
}
