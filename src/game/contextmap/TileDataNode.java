package game.contextmap;

import game.entity.Entity;
import game.tile.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TileDataNode wraps the underlying data of a Game Map Tile allowing the
 * addition of Entities on said Tile.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class TileDataNode {

    /**
     * The tile of this tile data node.
     */
    private final Tile tile;

    /**
     * The entities that are occupying this TileDataNode.
     */
    private final List<Entity> entities;

    /**
     * Construct a Node from a starting Tile.
     *
     * @param t Tile of this TileDataNode.
     */
    public TileDataNode(final Tile t) {
        this.tile = t;
        this.entities = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Construct a node from an existing Entity set.
     *
     * @param t Tile of this TileDataNode.
     * @param e Existing entities on this TileDataNode.
     */
    public TileDataNode(final Tile t,
                        final List<Entity> e) {
        this.tile = t;
        this.entities = e;
    }

    /**
     * @return Tile of this TileDataNode.
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * This represents the Column that the Tile resides in.
     *
     * @return X value of this tile.
     */
    public int getX() {
        return tile.getCol();
    }

    /**
     * This represents the Row that the Tile resides in.
     *
     * @return Y value of this tile.
     */
    public int getY() {
        return tile.getRow();
    }

    /**
     * @return All entities, if any that exist on this TileDataNode.
     */
    public Entity[] getEntities() {
        return entities.toArray(new Entity[0]);
    }

    /**
     * Add the provided entity to this tile data node.
     *
     * @param e The entity to add.
     */
    public synchronized void addEntity(final Entity e) {
        this.entities.add(e);
    }

    /**
     * Remove the provided entity from this tile data node.
     *
     * @param e The entity to remove.
     */
    public synchronized void removeEntity(final Entity e) {
        this.entities.remove(e);
    }

    /**
     * Removes all dead entities from the TileDataNode.
     */
    public synchronized void collectDeadEntities() {
        entities.removeIf(Entity::isDead);
    }
}
