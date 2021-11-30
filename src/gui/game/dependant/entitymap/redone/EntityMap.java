package gui.game.dependant.entitymap.redone;

import game.contextmap.CardinalDirection;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.HashMap;

/**
 * Simple Entity map for the game entities.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class EntityMap {

    // Simple ID -> Node  mapping system that will allow the update of a new
    // position based on the row col.

    /**
     * Map of ID values and their node representation.
     */
    private final HashMap<Long, ImageView> entityMap;

    /**
     * The root grid pane.
     */
    private final GridPane root;

    /**
     * @param rows The number of rows the map has.
     * @param cols The number of columns the map has.
     */
    public EntityMap(final int rows,
                     final int cols) {
        this.entityMap = new HashMap<>();
        this.root = new GridPaneFactory.CenteredGridPane().supply(rows, cols);
    }

    /**
     * @return The root object hierarchy for the scene.
     */
    public GridPane getRoot() {
        return this.root;
    }

    /**
     * @param id   The id value of the view to add.
     * @param view The node representation of the value.
     * @param row  The row position to display the node at (2d array indexing
     *             Row, Col)
     * @param col  The col position to display the node at.
     */
    public void addView(final long id,
                        final ImageView view,
                        int row,
                        int col) {
        this.entityMap.put(id, view);
        this.root.add(view, col, row);
    }

    /**
     * Update a values position to the provided position.
     *
     * @param id  The id value for the node.
     * @param row The new row for the node.
     * @param col The new column for the node.
     */
    public void setPosition(final long id,
                            final int row,
                            final int col) {
        final ImageView view = this.entityMap.get(id);
        this.root.getChildren().remove(view);
        this.root.add(view, col, row);
    }

    /**
     * Update a values position to the provided position, with direction.
     *
     * @param id  The id value for the node.
     * @param row The new row for the node.
     * @param col The new column for the node.
     * @param dir The direction in which to face the new node.
     */
    public void setPosition(final long id,
                            final int row,
                            final int col,
                            final CardinalDirection dir) {
        final ImageView view = this.entityMap.get(id);
        view.getRotate();
        int rotationAngle = switch (dir) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
        };

        view.setRotate(rotationAngle);
        this.root.getChildren().remove(view);
        this.root.add(view, col, row);
    }

    /**
     * Updates the image for the id of this node to the provided image.
     *
     * @param id The value to update.
     * @param image The new image to display.
     */
    public void setImage(final long id,
                         final Image image) {
        this.entityMap.get(id).setImage(image);
    }

    /**
     * Fetch the image for a provided node's id.
     *
     * @param id The value to get.
     * @return The image for the provided id.
     */
    public Image getImage(final long id) {
        return this.entityMap.get(id).getImage();
    }
}
