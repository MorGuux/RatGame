package gui.game.dependant.tilemap;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

/**
 * Game Node map which simplifies the tracking and modification of nodes at a
 * given Row, Col position.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class GameMap {

    /**
     * The underlying game map.
     */
    private final GridPane root;

    /**
     * Underlying node map that tracks all existing Nodes on the map by their
     * coordinate values.
     */
    private final Map<Coordinates<Integer>, Node> nodeMap;

    /**
     * Number of rows the map has.
     */
    private final int rowCount;

    /**
     * Number of columns the map has.
     */
    private final int colCount;

    /**
     * Constructs the Game Map from the provided Grid Size, and Grid
     * factory.
     *
     * @param rows    Number of Rows the Map has.
     * @param cols    Number of Columns the Map has.
     * @param factory GridPane construction object.
     */
    public GameMap(final int rows,
                   final int cols,
                   final GridPaneFactory factory) {
        this.rowCount = rows;
        this.colCount = cols;

        this.root = factory.supply(rowCount, colCount);
        this.nodeMap = new HashMap<>();
    }

    /**
     * Display the game map inside the given Pane.
     *
     * @param parent The Pane to display the game map inside.
     */
    public void displayIn(final Pane parent) {
        parent.getChildren().add(this.root);
    }

    /**
     * @return Root node scene object.
     */
    public GridPane getRoot() {
        return root;
    }

    /**
     * @return Number of Rows the Map has.
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @return Number of Columns the Map has.
     */
    public int getColCount() {
        return colCount;
    }

    /**
     * Sets the Node at the given coordinates to the provided Node. Does so
     * regardless of if there is a node there or not. Overwriting the node if
     * it does exist.
     *
     * @param row  Row to place at.
     * @param col  Col to place at.
     * @param node Node to place.
     * @throws IndexOutOfBoundsException If {@link #indexInBounds(int, int)}
     *                                   returns {@code false}.
     */
    public void setNodeAt(final int row,
                          final int col,
                          final Node node) {
        // If Node exists, replace that node with the given Node.
        if (this.containsNodeAt(row, col)) {
            final Node oldNode = this.getNodeAt(row, col);
            this.root.getChildren().remove(oldNode);

            // New node
            this.nodeMap.put(new Coordinates<>(row, col), node);
            this.root.add(node, col, row);

            // If Node doesn't exist, then check index limitations and then
            // add the node.
        } else {
            if (indexInBounds(row, col)) {
                this.nodeMap.put(new Coordinates<>(row, col), node);
                this.root.add(node, col, row);

            } else {
                throw new IndexOutOfBoundsException(
                        "Illegal position for a Node: "
                                + row
                                + ", "
                                + col
                );
            }
        }
    }

    /**
     * @param row Row to check index for.
     * @param col Col to check index for.
     * @return {@code true} if both row and col are inbounds. Otherwise, if
     * either the row or col are out of bounds then {@code false} is returned.
     */
    public boolean indexInBounds(final int row,
                                 final int col) {
        return (this.rowCount > row) && (this.colCount > col);
    }

    /**
     * Checks to see if there exists a Node at the given Row and Column Index.
     *
     * @param row Row to look at.
     * @param col Col to look at.
     * @return {@code true} if there exists something intersecting the Row
     * Col. Otherwise,{@code false} is returned.
     */
    public boolean containsNodeAt(final int row,
                                  final int col) {
        return this.nodeMap.containsKey(new Coordinates<>(row, col));
    }

    /**
     * @param row Row to look at
     * @param col Col to look at.
     * @return Node at the provided row, col.
     * @throws IndexOutOfBoundsException If {@link #indexInBounds(int, int)}
     *                                   returns {@code false}.
     * @throws IllegalStateException     If {@link #containsNodeAt(int, int)}
     *                                   returns {@code false}.
     */
    public Node getNodeAt(final int row,
                          final int col) {
        if (indexInBounds(row, col)) {
            if (containsNodeAt(row, col)) {
                return this.nodeMap.get(new Coordinates<>(row, col));

                // Existence issues
            } else {
                throw new IllegalStateException("");
            }

            // Index issues
        } else {
            throw new IndexOutOfBoundsException("");
        }
    }

    /**
     * Swaps the Nodes at the given positions with each other.
     *
     * @param row0 Row of first node.
     * @param col0 Col of first node.
     * @param row1 Row of second node.
     * @param col1 Col of second node.
     */
    public void swapNodeAt(final int row0,
                           final int col0,
                           final int row1,
                           final int col1) {
        if (containsNodeAt(row0, col0)
                && containsNodeAt(row1, col1)) {
            final Node l = getNodeAt(row0, col0);
            final Node r = getNodeAt(row1, col1);

            this.root.getChildren().remove(l);
            this.root.getChildren().remove(r);

            setNodeAt(row1, col1, l);
            setNodeAt(row0, col0, r);
            setNodeAt(row1, col1, l);

        } else {
            throw new IllegalStateException(
                    String.format(
                            "Swap not possible with: [%s, %s], [%s, %s]%n",
                            row0, col0,
                            row1, col1
                    )
            );
        }
    }
}
