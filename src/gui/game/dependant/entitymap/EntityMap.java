package gui.game.dependant.entitymap;

import game.contextmap.CardinalDirection;
import game.entity.Entity;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * Simple Entity map for the game entities.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public class EntityMap {


    // I've never used records they seem kinda useful
    /**
     * Map of ID values and their node representation.
     */
    private final HashMap<Long, EntityView> entityMap;
    /**
     * Maps a Node ID and the tiles that it should occupy/be displayed on.
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
                        final int row,
                        final int col) {
        this.entityMap.put(id, new EntityView(view, row, col));
        this.root.add(view, col, row);
    }

    /**
     * Remove the view for the provided ID from the map. And optionally
     * remove the views held within the occupation map as well.
     *
     * @param id             The id of the view to remove.
     * @param removeOccupied Should this remove the occupied tiles as well or
     *                       only the origin?
     */
    public void removeView(final long id,
                           final boolean removeOccupied) {
        // Remove the origin tile view
        if (this.entityMap.containsKey(id)) {
            this.root.getChildren().remove(
                    this.entityMap.get(id).getImageView()
            );
            this.entityMap.remove(id);
        }

        // Remove all occupied
        if (removeOccupied) {
            if (this.entityOccupyMap.containsKey(id)) {
                final List<EntityView> views = entityOccupyMap.get(id);

                views.forEach(i ->
                        this.root.getChildren().remove(i.getImageView())
                );

                this.entityMap.remove(id);
            }
        }
    }

    /**
     * Remove the view for the target ID at the provided position. This will
     * remove any and all at such a position.
     *
     * @param id  The id of the view to modify.
     * @param row The row position of the view.
     * @param col The column position of the view.
     */
    public void removeView(final long id,
                           final int row,
                           final int col) {
        if (this.entityOccupyMap.containsKey(id)) {
            final ListIterator<EntityView> views
                    = this.entityOccupyMap.get(id).listIterator();

            while (views.hasNext()) {
                final EntityView e = views.next();

                if (e.getRow() == row
                        && e.getCol() == col) {
                    this.root.getChildren().remove(e.getImageView());
                    views.remove();
                }
            }
        }
    }

    /**
     * Occupies the given row and column for the target ID if the ID exists
     * in the map.
     *
     * @param id   The id of the entity we're adding.
     * @param view The visual representation of the entity.
     * @param row  The row value of the entity.
     * @param col  The column value of the entity.
     * @throws IllegalStateException If the target ID does not exist in the
     *                               base entity map.
     */
    public void occupyPosition(final long id,
                               final ImageView view,
                               final int row,
                               final int col) {

        if (!this.entityMap.containsKey(id)) {
            throw new IllegalStateException();
        }

        if (!this.entityOccupyMap.containsKey(id)) {
            this.entityOccupyMap.put(id, new ArrayList<>());
        }
        this.entityOccupyMap.get(id).add(new EntityView(view, row, col));

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
        final int rotationAngle = switch (dir) {
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

    /**
     * Get the origin view that represents exactly where the entity of the
     * provided id exists.
     *
     * @param id The id of the entity.
     * @return The view that represents the entity.
     */
    public ImageView getOriginView(final long id) {
        return this.entityMap.get(id).getImageView();
    }

    /**
     * Internal wrapper class to assign extra information about an image view.
     *
     * @author Morgan Gardner
     * @version 0.1
     * Copyright: N/A
     */
    private record EntityView(ImageView imageView,
                              int row,
                              int col) {

        /**
         * @return The view for this entity.
         */
        public ImageView getImageView() {
            return imageView;
        }

        /**
         * @return The row position of the entity view.
         */
        public int getRow() {
            return row;
        }

        /**
         * @return The column position of the entity view.
         */
        public int getCol() {
            return col;
        }
    }
}