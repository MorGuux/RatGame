package gui.editor.module.grid.tileview;

/**
 * Java interface created on 24/02/2022 for usage in project RatGame-A2.
 *
 * @param <T> The type of the element that has changed in the grid.
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface GridUpdateListener<T> {

    /**
     * Called whenever an update to some part of the grid has occurred.
     *
     * @param row  The row position in the grid which was updated.
     * @param col  The column position in the grid that was updated.
     * @param elem The element at the position in the grid that was updated.
     * @return {@code true} if the target listener should remain in the list
     * of listeners. Else if it no longer requires updates to be tracked then
     * {@code false} should be returned.
     */
    boolean update(int row, int col, T elem);
}
