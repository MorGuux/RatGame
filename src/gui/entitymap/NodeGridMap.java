package gui.entitymap;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NodeGridMap<T extends Node> {

    /**
     * Maps a Node ID to a Grid mapper wrapper of a Node.
     */
    private final Map<Long, GridMapper<T>> entities;

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
     * @param tileSize The size of each tile in our grid.
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
     * @param nodeID ID of the Node that needs to be tracked.
     * @param node   The actual Node which is to be tracked.
     * @param initX  The initial Cartesian X position of the Node in a
     *               2D Array.
     * @param initY  The initial Cartesian X position of the Node in a
     *               2D Array.
     */
    public void trackNode(final Long nodeID,
                          final T node,
                          final int nodeSize,
                          final int initX,
                          final int initY) {
        if (entities.containsKey(nodeID)) {
            throw new IllegalStateException("Entity already exists!");
        } else {
            entities.put(
                    nodeID,
                    new GridMapper<>(node, this.tileSize, nodeSize, initX, initY)
            );
            this.displayPane.getChildren().add(node);
        }
    }

    /**
     *
     * @param nodeID
     * @param x
     * @param y
     */
    public void setNodePosition(final Long nodeID,
                                final int x,
                                final int y) {
        if (containsNodeID(nodeID)) {
            final GridMapper<T> node = entities.get(nodeID);

            if (node.isValidPosition(x, y)) {
                node.setPosition(x, y, 300);

            } else {
                throw new IllegalStateException("Invalid Position: " + x + ", " + y);
            }
        }
    }

    /**
     * @param nodeID ID to check for existence.
     * @return {@code true} if and only if the provided ID refers to some Value.
     * Otherwise, {@code false}.
     */
    public boolean containsNodeID(final Long nodeID) {
        return this.entities.containsKey(nodeID);
    }
}
