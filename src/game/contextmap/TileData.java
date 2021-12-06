package game.contextmap;

import game.entity.Entity;
import game.tile.Tile;

import java.util.Objects;

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
     * @return Column of this tile.
     */
    public int getCol() {
        return this.node.getCol();
    }

    /**
     * This represents the Row that the Tile resides in.
     *
     * @return Row of this tile.
     */
    public int getRow() {
        return this.node.getRow();
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

    /**
     * @return String containing info about the tile position
     */
    @Override
    public String toString() {
        return "TileData: Row: " + getRow() + ", Col: " + getCol();
    }

    /**
     * @return Hashcode of this node based on its internal data.
     */
    @Override
    public int hashCode() {
        return Objects.hash(node.getRow(), node.getCol(), node.getTile());
    }

    /**
     * Checks if TileData is equal to an object passed.
     * @param obj object to be checked
     * @return true if obj is TileData instance and coordinates are the same,
     * false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TileData x) {

            return (this.getRow() == x.getRow())
                    && (this.getCol() == x.getCol());
        }
        return false;
    }
}
