package gui.game.dependant.entitymap;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Node Grid Map stores the positions of some Nodes and allows systematic
 * updating of the Pixel positions of the nodes relative to a grid.
 *
 * @param <K> The type of the keys to identify values in this map.
 * @param <V> The Type of {@link Node} to stored in this Map.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class NodeGridMap<K, V extends Node> {

    /**
     * Maps a Node ID to a Grid mapper wrapper of a Node.
     */
    private final Map<K, GridMapper<V>> entities;

    /**
     * Pane which all entities are displayed in.
     */
    private final Pane displayPane;

    /**
     * Size of each tile in our grid i.e., 32x32 (pixels).
     */
    private final int tileSize;

    /**
     * Constructs a grid map from the given Display pane and the
     * Tile size.
     *
     * @param displayPane Pane to display our Nodes in.
     * @param tileSize    The size of each tile in our grid.
     */
    public NodeGridMap(final Pane displayPane,
                       final int tileSize) {
        Objects.requireNonNull(displayPane);
        this.entities = new HashMap<>();
        this.displayPane = displayPane;
        this.tileSize = tileSize;
    }

    /**
     * Adds the provided node to our grid.
     *
     * @param key   ID of the Node that needs to be tracked.
     * @param value The actual Node which is to be tracked.
     * @param initX The initial Cartesian X position of the Node in a
     *              2D Array.
     * @param initY The initial Cartesian X position of the Node in a
     *              2D Array.
     * @throws IllegalStateException If the Key already exists in this Grid map.
     */
    public void trackNode(final K key,
                          final V value,
                          final int nodeSize,
                          final int initX,
                          final int initY) {
        if (entities.containsKey(key)) {
            throw new IllegalStateException("Entity already exists!");
        } else {
            final GridMapper<V> mapper = new GridMapper<>(
                    value,
                    this.tileSize,
                    nodeSize,
                    initX,
                    initY
            );
            entities.put(
                    key,
                    mapper
            );
            this.displayPane.getChildren().add(value);
        }
    }

    /**
     * Updates a nodes position to a new Cartesian/2D Array value.
     *
     * @param key Key of the node to update.
     * @param x   The new Cartesian X position of the Node.
     * @param y   The new Cartesian Y position of the Node.
     */
    public void setNodePosition(final K key,
                                final int x,
                                final int y) {
        // todo 300 duration is weird; hardcode it somewhere else, or make it
        //  a parameters to the method.
        if (containsNodeID(key)) {
            final GridMapper<V> node = entities.get(key);

            if (node.isValidPosition(x, y)) {
                node.setPosition(x, y, 300);

            } else {
                // todo proper exceptions
                throw new IllegalStateException(
                        "Invalid Position: "
                                + x
                                + ", "
                                + y);
            }
        }
    }

    /**
     * @param key ID to check for existence.
     * @return {@code true} if and only if the provided ID refers to some Value.
     * Otherwise, {@code false}.
     */
    public boolean containsNodeID(final K key) {
        return this.entities.containsKey(key);
    }
}
