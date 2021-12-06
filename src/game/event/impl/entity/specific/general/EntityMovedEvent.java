package game.event.impl.entity.specific.general;

import game.contextmap.CardinalDirection;
import game.entity.Entity;
import game.event.impl.entity.EntityEvent;

/**
 * Wraps an event where some Entity moves to another tile.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class EntityMovedEvent extends EntityEvent {

    /**
     * The old row value of the entity.
     */
    private final int oldRow;

    /**
     * The old col value of the entity.
     */
    private final int oldCol;

    /**
     * The time it takes to go from Old pos to New pos.
     */
    private final int timeFrame;

    /**
     * The new row value of the entity.
     */
    private final int newRow;
    /**
     * The new col value of the entity.
     */
    private final int newCol;

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author    The target entity.
     * @param oldRowPosition    The old row position for the entity.
     * @param oldColPosition    The old Col position for the entity.
     * @param moveTimeFrame The time in milliseconds the move should take to
     *                  reach its destination (How fast it moves).
     */
    public EntityMovedEvent(final Entity author,
                            final int oldRowPosition,
                            final int oldColPosition,
                            final int moveTimeFrame) {
        super(author);
        this.oldRow = oldRowPosition;
        this.oldCol = oldColPosition;
        this.timeFrame = moveTimeFrame;
        this.newRow = author.getRow();
        this.newCol = author.getCol();
    }

    /**
     * @return The old row position for the entity.
     */
    public int getOldRow() {
        return oldRow;
    }

    /**
     * @return The old col position for the entity.
     */
    public int getOldCol() {
        return oldCol;
    }

    /**
     * @return How long the entity should take to get to the new position.
     */
    public int getTimeFrame() {
        return timeFrame;
    }

    /**
     * Get the direction the entity moved in, from their old position to
     * their new position.
     * @return The direction the entity moved in.
     */
    public CardinalDirection getDirection() {
        return CardinalDirection.getTravelDirection(
                this.newRow,
                this.newCol,
                this.oldRow,
                this.oldCol
        );
    }
}
