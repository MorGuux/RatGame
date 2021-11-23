package game.entity.subclass.poison;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.entity.Item;

/**
 * Poison.java - A poison item.
 * Uses the Entity class as a base.
 * Upon contact by a Rat, it will poison the Rat, killing it. It will then be
 * removed from the game.
 *
 * @author Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */

public class Poison extends Item {

    /**
     * Construct a Poison item from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public Poison(final int initX,
                  final int initY) {
        super(initX, initY);
    }

    /**
     * Construct a Poison item from the base starting x, y, and health values.
     *
     * @param initX     X position in a 2D Array.
     * @param initY     Y position in a 2D Array.
     * @param curHealth Current health of the item.
     */
    public Poison(final int initX,
                  final int initY,
                  final int curHealth) {
        super(initX, initY, curHealth);
    }

    /**
     * Place where this Poison item can be updated and, do something once
     * provided some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this Poison item.
     * @implNote Both Objects are Object because we don't have
     * implementations for these objects just yet.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        //TODO : Implement poison update. Will check for contact with a rat
        // and if so, will kill it. It will then remove itself from the game.
    }

    /**
     * Build the Poison item to a String that can be saved to a File; all
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
