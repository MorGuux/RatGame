package game.entity.subclass.rat;

import game.entity.Entity;

/**
 * Rat.java - A rat entity.
 * Uses the Entity class as a base.
 * Uses the MovementHandler class to handle autonomous movement around the
 * map, with random direction choices. It will interact with other rats to
 * mate, and with items that can damage and change properties of it.
 *
 * @author Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */

public class Rat extends Entity {

    /**
     * Represents the sex of a rat.
     */
    private enum Sex {
        /**
         * Male rat. It can mate with other female rats.
         */
        MALE,
        /**
         * Female rat. It can mate with other male rats and give birth once
         * fertile for a set amount of time.
         */
        FEMALE
    }

    /**
     * Represents the age of the rat.
     */
    private enum Age {
        /**
         * Baby rat. It can grow into an adult after a set amount of time has
         * passed.
         */
        BABY,
        /**
         * Adult rat. Its sprite size is increased, and it can mate with
         * other rats.
         */
        ADULT
    }

    /**
     * The current sex of the rat (male/female).
     */
    private Sex sex;

    /**
     * The current age of the rat (baby/adult).
     */
    private Age age;

    /**
     * If the rat can produce offspring (give birth).
     */
    private boolean isFertile;

    /**
     * The current time (ms) left before the rat grows into an adult rat
     * (from a baby).
     */
    private long timeToAge;

    //private MovementHandler movementHandler

    /**
     * Construct a Rat from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public Rat(final int initX,
               final int initY) {
        super(initX, initY);
    }

    /**
     * Construct a Rat from the base starting x, y, and health values.
     *
     * @param initX     X position in a 2D Array.
     * @param initY     Y position in a 2D Array.
     * @param curHealth Current health of the Entity.
     */
    public Rat(final int initX,
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
     * Returns the sex of the rat.
     * @return The current sex of the rat
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * Sets the sex of the rat to a given Sex.
     * @param sex The sex of the rat
     */
    public void setSex(final Sex sex) {
        this.sex = sex;
    }

    /**
     * Returns the age of the rat.
     * @return The current age of the rat
     */
    public Age getAge() {
        return age;
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
