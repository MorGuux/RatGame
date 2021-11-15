package game.entity.subclass.bomb;

import game.entity.Item;

import java.net.URL;

/**
 * Bomb Game Item object that once placed into the game will countdown on
 * each update until finally exploding; killing any and all Entities when it
 * does explode.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class Bomb extends Item {

    /**
     * Time in milliseconds all bombs will explode after.
     */
    private static final int EXPLODE_TIME = 5_000;

    /**
     * Bomb image resource.
     */
    private static final URL BOMB_IMAGE
            = Bomb.class.getResource("BombImage.jpg");

    /**
     * Bomb explode image resource.
     */
    private static final URL BOMB_EXPLODE_IMAGE
            = Bomb.class.getResource("BombExplodeImage.jpg");

    /**
     * Current time before the time explodes.
     */
    private int currentTime;

    /**
     * Construct an Entity from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public Bomb(final int initX,
                final int initY) {
        super(initX, initY);
        currentTime = EXPLODE_TIME;
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initX     X position in a 2D Array.
     * @param initY     Y position in a 2D Array.
     * @param curHealth Current health of the Entity.
     */
    public Bomb(int initX, int initY, int curHealth) {
        super(initX, initY, curHealth);
        currentTime = EXPLODE_TIME;
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
        // todo complete at some point
    }

    /**
     * Build the Entity to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Entity class.
     * @implNote Context map is Object since we don't have an implementation
     * of it yet.
     */
    @Override
    public String buildToString(final Object contextMap) {
        // todo complete at some point
        return null;
    }
}