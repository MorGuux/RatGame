package game.entity.subclass.noentry;

import game.entity.Item;

/**
 * No Entry Item blocks any Entities and Items from moving onto the same tile
 * as it. Persists until enough interactions have occurred which then it will
 * no longer persist.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class NoEntry extends Item {

    /**
     * Construct an Entity from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public NoEntry(final int initX,
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
    public NoEntry(final int initX,
                   final int initY,
                   final int curHealth) {
        super(initX, initY, curHealth);
    }

    /**
     * Place where this entity can be updated and, do something once provided
     * some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this entity.
     * @implNote Both Objects are Object because we don't have
     * implementations for these objects just yet.
     */
    @Override
    public void update(final Object contextMap,
                       final Object ratGame) {

    }

    /**
     * Build the Entity to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Entity class.
     * @return String or args which can be used to construct this specific
     * state of the Object.
     * @implNote Context map is Object since we don't have an implementation
     * of it yet.
     */
    @Override
    public String buildToString(final Object contextMap) {
        return null;
    }

    /**
     * Damages an Entity by the provided amount. Unless the damage is fatal
     * in which then it will just {@link #kill()} the Entity instead.
     *
     * @param damage The amount of damage to deal to the Entity.
     */
    @Override
    public void damage(final int damage) {
        super.damage(damage);
    }
}
