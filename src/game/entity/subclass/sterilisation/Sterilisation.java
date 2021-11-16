package game.entity.subclass.sterilisation;

import game.entity.Item;

/**
 * Sterilisation.java - A sterilisation item.
 * Uses the Entity class as a base.
 * Once placed, after a certain amount of time, all rats within a certain
 * radius will be inhibited from breeding for a duration of time.
 *
 * @author Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */

public class Sterilisation extends Item {

    /**
     * Construct a Sterilisation item from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public Sterilisation(final int initX,
                         final int initY) {
        super(initX, initY);
    }

    /**
     * Construct a Sterilisation item from the base starting x, y, and health
     * values.
     *
     * @param initX     X position in a 2D Array.
     * @param initY     Y position in a 2D Array.
     * @param curHealth Current health of the item.
     */
    public Sterilisation(final int initX,
                         final int initY,
                         final int curHealth) {
        super(initX, initY, curHealth);
    }

    /**
     * Place where this Sterilisation item can be updated and, do something once
     * provided some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this Sterilisation item.
     * @implNote Both Objects are Object because we don't have
     * implementations for these objects just yet.
     */
    @Override
    public void update(final Object contextMap,
                       final Object ratGame) {
        //TODO : Implement sterilisation update. Will request all rats within
        // a radius of this item and sterilise them (set isFertile to false)
        // after a set duration.
    }

    /**
     * Build the Sterilisation item to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Poison class.
     * @implNote Context map is Object since we don't have an implementation
     * of it yet.
     */
    @Override
    public String buildToString(final Object contextMap) {
        return null;
    }
}
