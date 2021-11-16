package game.entity.subclass.deathRat;

import game.entity.Entity;

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

public class DeathRat extends Entity {
    //private MovementHandler movementHandler

    /**
     * Construct a Death Rat from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public DeathRat(final int initX,
                    final int initY) {
        super(initX, initY);
    }

    /**
     * Construct a Death Rat from the base starting x, y, and health values.
     *
     * @param initX     X position in a 2D Array.
     * @param initY     Y position in a 2D Array.
     * @param curHealth Current health of the Entity.
     */
    public DeathRat(final int initX,
                    final int initY,
                    final int curHealth) {
        super(initX, initY, curHealth);
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
    public void update(Object contextMap, Object ratGame) {
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
    public String buildToString(Object contextMap) {
        //TODO : Implement buildToString to create a string that can be saved
        // in a file.
        return null;
    }
    /**
     * Damage the rat by a given value. If the resulting health falls below
     * or equal to 0, the rat is killed.
     * @param damage The amount of damage to deal to the rat.
     */
    public void damage(final int damage) {
        int health = super.getHealth();
        health -= damage;
        super.setHealth(health);

        if (health <= 0) {
            super.kill();
        }
    }
}