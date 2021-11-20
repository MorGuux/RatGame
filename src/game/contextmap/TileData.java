package game.contextmap;

import game.entity.Entity;
import game.tile.Tile;

/**
 * Wrapper class designed to give controlled access to the entities existing
 * on a Tile. Things such as the {@link Tile} and the {@link Entity} that are
 * either occupying, or standing on the tile itself.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class TileData {

    /**
     * The underlying node that we're giving controlled access to.
     */
    private final TileDataNode node;

    /**
     * Constructs a TileData from the provided node.
     *
     * @param node The node to give controlled access to.
     */
    public TileData(final TileDataNode node) {
        this.node = node;
    }

    /**
     * This represents the Column that the Tile resides in.
     *
     * @return X value of this tile.
     */
    public int getX() {
        return this.node.getX();
    }

    /**
     * This represents the Row that the Tile resides in.
     *
     * @return Y value of this tile.
     */
    public int getY() {
        return this.node.getY();
    }

    /**
     * @return Tile of this TileData.
     */
    public Tile getTile() {
        return this.node.getTile();
    }

    /**
     * @return All entities that exist on this TileData, even if it is none.
     */
    public Entity[] getEntities() {
        return this.node.getEntities();
    }
}
