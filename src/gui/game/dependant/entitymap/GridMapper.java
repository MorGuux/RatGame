package gui.game.dependant.entitymap;

import javafx.animation.PathTransition;
import javafx.scene.Node;
import javafx.scene.shape.Line;

//todo improve this

/**
 * Wraps some Node and allows its Pixel, 'x', and 'y' to be
 * modified to make it relative to it's position on a 2D
 * Array. Does so, smoothly with a Node translation.
 *
 * @param <T> The node which should be the target for
 *            the X, and Y translations.
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class GridMapper<T extends Node> {

    private final T object;
    private final int gridSize;
    private final int nodeSize;
    private int curX;
    private int curY;

    /**
     * @param obj      The target object to move around.
     * @param gridSize The grid size to which we
     *                 will move around in.
     * @param nodeSize The size of the Node; Should
     *                 keep this square (32x32) so
     *                 that the translations are
     *                 centered.
     * @param initX    The initial Cartesian X position
     *                 of the node.
     * @param initY    The initial Cartesian X position
     *                 of the node.
     */
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
                            final int y,
                            final long duration) {
        if (isValidPosition(x, y)) {
            if (x == curX) {
                // Translate y
                translate(getCenter(curX), getCenter(y), x, y, duration);
                this.curY = y;

                // Translate x
            } else {
                translate(getCenter(x), getCenter(curY), x, y, duration);
                this.curX = x;
            }
        } else {
            throw new IllegalStateException("Invalid Position: " + x + ", " + y);
        }
    }

    //todo clean up
    public void translate(final int pixelX,
                          final int pixelY,
                          final int newX,
                          final int newY,
                          final long duration) {

        // Will need to restructure to support this properly
        final Line line = new Line();
        line.setStartX((gridSize * curX) + ((double) nodeSize / 2));
        line.setStartY((gridSize * curY) + ((double) nodeSize / 2));

        line.setEndX((gridSize * newX) + ((double) nodeSize / 2));
        line.setEndY((gridSize * newY) + ((double) nodeSize / 2));

        final PathTransition transition = new PathTransition();
        transition.setNode(object);
        transition.setPath(line);
        transition.play();
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
}
