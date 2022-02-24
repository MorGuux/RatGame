package gui.editor.module.grid.tileview;

/**
 * Java interface created on 24/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public interface GridUpdateListener<T> {

    /**
     * Called whenever an update to some part of the grid has occurred.
     *
     * @param row  The row position in the grid which was updated.
     * @param col  The column position in the grid that was updated.
     * @param elem The element at the position in the grid that was updated.
     */
    void update(int row, int col, T elem);
}
