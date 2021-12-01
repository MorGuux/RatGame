package game.entity;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.event.GameActionListener;
import game.event.GameEvent;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
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
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    /**
     * Default health value for all Entities.
     */
    private static final int DEFAULT_HEALTH = 100;

    /**
     * The x position of this Entity in a 2D Array.
     */
    private int row;

    /**
     * The y position of this Entity in a 2D Array.
     */
    private int col;

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
     * Construct an Entity from the base starting Row and Column.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     */
    public Entity(final int initRow,
                  final int initCol) {
        this(initRow, initCol, DEFAULT_HEALTH);
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public Entity(final int initialRow,
                  final int initialCol,
                  final int curHealth) {
        this.row = initialRow;
        this.col = initialCol;
        this.health = curHealth;

        this.entityID = ID_GENERATOR.getAndIncrement();
    }

    /**
     * @return The Entities current X position.
     */
    public int getRow() {
        return row;
    }

    /**
     * Update the Row value of this entity to the provided Row value.
     * <p>
     * Note that updates should never exceed more than a single value for any
     * single update.
     *
     * @param newRow The new Row value for this entity.
     */
    protected void setRow(final int newRow) {
        this.row = newRow;
    }

    /**
     * @return The Entities current Y position.
     */
    public int getCol() {
        return col;
    }

    /**
     * Update the Col value of this Entity to the provided Col value.
     *
     * @param newCol The new Col value for this Entity.
     * @see #setRow(int)
     */
    protected void setCol(final int newCol) {
        this.col = newCol;
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
     * Damages an Entity by the provided amount. Unless the damage is fatal
     * in which then it will just {@link #kill()} the Entity instead.
     *
     * @param damage The amount of damage to deal to the Entity.
     */
    protected void damage(final int damage) {
        // If damage fatal, just kill
        if (damage >= this.getHealth()) {
            this.kill();

            // Else subtract
        } else {
            this.setHealth(this.getHealth() - damage);
        }
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
    protected void fireEvent(final GameEvent<?> event) {
        Objects.requireNonNull(event);
        listener.onAction(event);
    }

    /**
     * From a given set of occupied tiles and a target entity this will
     * produce a string joined at commas of the positions formatted as (row,
     * col); this excludes the origin point.
     *
     * @param data The set of tiles to parse.
     * @param e    The entity we're parsing positions of.
     * @return Formatted string.
     */
    protected static String formatPositions(final TileData[] data,
                                            final Entity e) {
        final String template = "(%s,%s)";
        final StringJoiner sj = new StringJoiner(",");

        Arrays.stream(data)
                // Only those !origin
                .filter(i -> ((i.getRow() != e.getRow())
                        || i.getCol() != e.getCol()))

                // Produce (row,col) string
                .forEach(i -> sj.add(String.format(
                        template, i.getRow(), i.getCol()
                )));

        return sj.toString();
    }

    /**
     * Formats the Entity information of this Entity into a single string.
     *
     * @return Formatted string of the Entity information.
     */
    @Override
    public String toString() {
        return String.format("%s: (%s, %s), %s",
                this.getClass().getSimpleName(),
                this.getRow(),
                this.getCol(),
                this.getHealth()
        );
    }

    /**
     * Place where this entity can be updated and, do something once provided
     * some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this entity.
     */
    public abstract void update(ContextualMap contextMap, RatGame ratGame);

    /**
     * Build the Entity to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Entity class.
     * @return String or args which can be used to construct this specific
     * state of the entity.
     */
    public abstract String buildToString(ContextualMap contextMap);

    /**
     * Context for the entity, where it clarifies if it is hostile or not.
     * For example: Rat is hostile; DeathRat is not hostile.
     *
     * @return true if the entity is hostile, false otherwise
     */
    public abstract boolean isHostile();

    /**
     * @return Get the sprite that represents the Entities current state
     * visually.
     */
    public abstract URL getDisplaySprite();

    /**
     * @return The number of points to award when this entity is killed.
     */
    public abstract int getDeathPoints();
}
