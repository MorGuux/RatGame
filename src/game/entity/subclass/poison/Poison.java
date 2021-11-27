package game.entity.subclass.poison;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.entity.Item;
import game.entity.subclass.deathRat.DeathRat;

import java.net.URL;

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
     * Builds a Bomb object from the provided args string.
     *
     * @param args Arguments used to build a bomb.
     * @return Newly constructed Bomb.
     */
    public static Poison build(final String[] args) {
        return null;
    }

    /**
     * Construct an Entity from the base starting Row and Column.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     */
    public Poison(final int initRow,
                  final int initCol) {
        super(initRow, initCol);
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public Poison(final int initialRow,
                  final int initialCol,
                  final int curHealth) {
        super(initialRow, initialCol, curHealth);
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
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    @Override
    public URL getDisplaySprite() {
        return null;
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
