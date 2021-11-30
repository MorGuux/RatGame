package gui.game.dependant.entitymap.redone;

import game.contextmap.CardinalDirection;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private final HashMap<Long, EntityView> entityMap;

    /**
     * Maps a Node ID and the tiles that is should occupy/be displayed on.
     */
    private final HashMap<Long, List<EntityView>> entityOccupyMap;

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
        this.entityOccupyMap = new HashMap<>();
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
        this.entityMap.put(id, new EntityView(view, row, col));
        this.root.add(view, col, row);
    }

    public void occupyPosition(final long id,
                               final ImageView view,
                               final int row,
                               final int col) {
        if (!this.entityOccupyMap.containsKey(id)) {
            this.entityOccupyMap.put(id, new ArrayList<>());
        }
        this.entityOccupyMap.get(id).add(new EntityView(view, row, col));

        this.root.add(view, col, row);
    }

    public void deOccupyPosition(final long id, final int row, final int col) {

        if (this.entityOccupyMap.containsKey(id)) {
            if (this.entityMap.get(id).getRow() == row
                    && this.entityMap.get(id).getCol() == col) {
                this.root.getChildren().remove(this.entityMap.get(id).getImageView());
                this.entityOccupyMap.remove(id);
            }
        }
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
        final ImageView view = this.entityMap.get(id).getImageView();
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
        final ImageView view = this.entityMap.get(id).getImageView();
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
     * @param id    The value to update.
     * @param image The new image to display.
     */
    public void setImage(final long id,
                         final Image image) {
        this.entityMap.get(id).getImageView().setImage(image);
    }

    /**
     * Fetch the image for a provided node's id.
     *
     * @param id The value to get.
     * @return The image for the provided id.
     */
    public Image getImage(final long id) {
        return this.entityMap.get(id).getImageView().getImage();
    }

    public class EntityView {
        private final ImageView imageView;
        private final int row;
        private final int col;

        public EntityView(final ImageView imageView,
                          final int row,
                          final int col) {
            this.imageView = imageView;
            this.row = row;
            this.col = col;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }
}
