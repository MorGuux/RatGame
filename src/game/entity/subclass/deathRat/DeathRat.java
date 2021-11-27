package game.entity.subclass.deathRat;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.entity.Item;
import game.entity.subclass.bomb.Bomb;

/**
 * Rat.java - A death rat entity.
 * Uses the Entity class as a base.
 * Uses the MovementHandler class to handle autonomous movement around the
 * map, with random direction choices. It will interact with other rats by
 * murdering them mercilessly
 *
 * @author Maksim Samokhvalov
 * @version 0.1
 * Copyright: N/A
 */

public class DeathRat extends Item {
    //private MovementHandler movementHandler

    /**
     * Builds a Bomb object from the provided args string.
     *
     * @param args Arguments used to build a bomb.
     * @return Newly constructed Bomb.
     */
    public static DeathRat build(final String[] args) {
        return null;
    }

    /**
     * Construct an Entity from the base starting Row and Column.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     */
    public DeathRat(final int initRow,
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
    public DeathRat(final int initialRow,
                    final int initialCol,
                    final int curHealth) {
        super(initialRow, initialCol, curHealth);
    }

    /**
     * Place where this rat can be updated and, do something once provided
     * some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this entity.
     * @implNote Both Objects are Object because we don't have
     * implementations for these objects just yet.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        //TODO : Implement rat update, utilising movementHandler to move the
        // rat within the level.
    }

    /**
     * Build the Rat to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Rat class.
     * @implNote Context map is Object since we don't have an implementation
     * of it yet.
     */
    @Override
    public String buildToString(final Object contextMap) {
        //TODO : Implement buildToString to create a string that can be saved
        // in a file.
        return null;
    }

    // Removed the damage method in here as the DeathRat would never be
    // damaged by other entities, only itself, or a bomb, which is an instant
    // kill.


    /**
     * Kills the Death Rat.
     */
    @Override
    public void kill() {
        super.kill();
    }
}
