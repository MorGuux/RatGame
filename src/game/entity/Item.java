package game.entity;

/**
 * Item Class encapsulates Player, placed items. They don't differ at all
 * from regular Entities but are identifiably different.
 *
 * @author -Ry, Shashank Jain
 * @version 0.1
 * Copyright: N/A
 */
public abstract class Item extends Entity {

    /**
     * Construct an Entity from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public Item(final int initX,
                final int initY) {
        super(initX, initY);
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initX     X position in a 2D Array.
     * @param initY     Y position in a 2D Array.
     * @param curHealth Current health of the Entity.
     */
    public Item(final int initX,
                final int initY,
                final int curHealth) {
        super(initX, initY, curHealth);
    }

    @Override
    public final boolean isHostile() {
        return false;
    }
}
