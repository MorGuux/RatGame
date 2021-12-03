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
     * Constructs an entity event from the target entity.
     *
     * @param author    The target entity.
     * @param oldRow    The old row position for the entity.
     * @param oldCol    The old Col position for the entity.
     * @param timeFrame The time in milliseconds the move should take to
     *                  reach its destination (How fast it moves).
     */
    public EntityMovedEvent(final Entity author,
                            final int oldRow,
                            final int oldCol,
                            final int timeFrame) {
        super(author);
        this.oldRow = oldRow;
        this.oldCol = oldCol;
        this.timeFrame = timeFrame;
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

    public CardinalDirection getDirection() {
        return CardinalDirection.getTravelDirection(
                this.getRow(),
                this.getCol(),
                this.oldRow,
                this.oldCol
        );
    }
}
