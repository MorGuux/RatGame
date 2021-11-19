package game.entity;

import game.event.GameActionListener;
import game.event.GameEvent;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base Entity Class that wraps all of our game Objects. Anything that wants
 * to exist in the map and do 'something' is required to inherit from this
 * Class.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public abstract class Entity {

    /**
     * Entity ID Generator; Atomic means that even if multiple threads
     * create an Entity, each entity will be given a unique number.
     */
    private static final AtomicLong idGenerator = new AtomicLong();

    /**
     * The x position of this Entity in a 2D Array.
     */
    private int x;

    /**
     * The y position of this Entity in a 2D Array.
     */
    private int y;

    /**
     * The health value of this Entity in a 2D Array. Should max out at 100
     * however there is no actual cap enforced here.
     */
    private int health;

    /**
     * The action listener to which all Entity events will be passed into.
     */
    private GameActionListener listener;

    /**
     * The Unique ID of this Entity.
     */
    private final long entityID;

    /**
     * Construct an Entity from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public Entity(final int initX,
                  final int initY) {
        this(initX, initY, 100);
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initX     X position in a 2D Array.
     * @param initY     Y position in a 2D Array.
     * @param curHealth Current health of the Entity.
     */
    public Entity(final int initX,
                  final int initY,
                  final int curHealth) {
        this.x = initX;
        this.y = initY;
        this.health = curHealth;

        this.entityID = idGenerator.getAndIncrement();
    }

    /**
     * @return The Entities current X position.
     */
    public int getX() {
        return x;
    }

    /**
     * Update the X value of this entity to the provided X value.
     * <p>
     * Note that updates should never exceed more than a single value for any
     * single update.
     *
     * @param newX The new X value for this entity.
     */
    protected void setX(final int newX) {
        this.x = newX;
    }

    /**
     * @return The Entities current Y position.
     */
    public int getY() {
        return y;
    }

    /**
     * Update the Y value of this Entity to the provided Y value.
     *
     * @param newY The new Y value for this Entity.
     * @see #setX(int)
     */
    protected void setY(final int newY) {
        this.y = newY;
    }

    /**
     * @return The Entities current health value.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Set the Entity health value to the provided value.
     *
     * @param newHealth The new health value for this entity.
     */
    protected void setHealth(final int newHealth) {
        this.health = newHealth;
    }

    /**
     * Convenience method to kill this Entity.
     */
    protected void kill() {
        this.health = 0;
    }

    /**
     * @return Unique Entity ID value.
     */
    public long getEntityID() {
        return entityID;
    }

    /**
     * @return {@code true} if this Entity is considered 'dead' if the Entity
     * is not considered dead then {@code false} is returned.
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Set the Game event Listener to the provided listener.
     *
     * @param listener The listener to use here.
     */
    public void setListener(final GameActionListener listener) {
        this.listener = listener;
    }

    /**
     * Fires of the provided Entity event.
     *
     * @param event The event to fire.
     */
    protected void fireEvent(final GameEvent event) {
        Objects.requireNonNull(event);
        listener.onAction(event);
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
    public abstract void update(Object contextMap, Object ratGame);

    /**
     * Build the Entity to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Entity class.
     * @implNote Context map is Object since we don't have an implementation
     * of it yet.
     */
    public abstract String buildToString(Object contextMap);
}
