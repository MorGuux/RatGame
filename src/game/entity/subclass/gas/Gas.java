package game.entity.subclass.gas;

import game.entity.Item;

/**
 * Gas.java - A gas item.
 * Uses the Entity class as a base.
 * Once placed, it will start spreading at specific speed, killing all
 * rats that have been within the impact radius for a given amount of time.
 * It can then be removed from the game.
 *
 * @author Ashraf Said
 * @version 0.1
 * Copyright: N/A
 */

public class Gas extends Item {

    /**
     * Construct a Gas item from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public Gas(final int initX,
               final int initY) {
        super(initX, initY);
    }

    /**
     * Construct a Gas item from the base starting x, y, and health values.
     *
     * @param initX     X position in a 2D Array.
     * @param initY     Y position in a 2D Array.
     * @param curHealth Current health of the item.
     */
    public Gas(final int initX,
               final int initY,
               final int curHealth) {
        super(initX, initY, curHealth);
    }

    /**
     * Place where this Gas item can be updated and, do something once
     * provided some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this Gas item.
     * @implNote Both Objects are Object because we don't have
     * implementations for these objects just yet.
     */
    @Override
    public void update(final Object contextMap,
                       final Object ratGame) {
        //TODO : Implement gas update. Will request all rats within
        // a radius of this item and will kill them after a given set of time
        // (gradually lowers health) after a set duration.
    }

    /**
     * Build the Gas item to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Gas class.
     * @implNote Context map is Object since we don't have an implementation
     * of it yet.
     */
    @Override
    public String buildToString(final Object contextMap) {
        return null;
    }
}
