package game.event.impl.entity;

import game.entity.Entity;
import game.event.GameEvent;

/**
 * Wraps a generic entity event of a target entity.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public abstract class EntityEvent extends GameEvent<Entity> {

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author The target entity.
     */
    public EntityEvent(final Entity author) {
        super(author);
    }

    /**
     * @return The ID Value of the target entity.
     */
    public long getEntityID() {
        return getEventAuthor().getEntityID();
    }

    /**
     * @return The row position in a grid where the author entity exists.
     */
    public int getRow() {
        return this.getEventAuthor().getRow();
    }

    /**
     * @return The column position in a grid where the author entity exists.
     */
    public int getCol() {
        return this.getEventAuthor().getCol();
    }

    /**
     * @return Current health of the entity.
     */
    public int getHealth() {
        return this.getEventAuthor().getHealth();
    }

    /**
     * @return The author entity to string.
     */
    public String toString() {
        return getEventAuthor().toString();
    }
}
