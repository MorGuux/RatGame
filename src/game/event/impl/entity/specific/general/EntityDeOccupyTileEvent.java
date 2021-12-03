package game.event.impl.entity.specific.general;

import game.entity.Entity;
import game.event.impl.entity.EntityEvent;

import java.net.URL;

/**
 * Event wraps when an Entity de-occupies a tile.
 *
 * @author Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */
public class EntityDeOccupyTileEvent extends EntityEvent {

    /**
     * The row that was de-occupied.
     */
    private final int deoccupiedRow;

    /**
     * The column that was de-occupied.
     */
    private final int deoccupiedCol;

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author        The target entity.
     * @param deoccupiedRow   The row that was de-occupied.
     * @param deoccupiedCol   The column that was de-occupied.
     */
    public EntityDeOccupyTileEvent(final Entity author,
                                   final int deoccupiedRow,
                                   final int deoccupiedCol) {
        super(author);
        this.deoccupiedRow = deoccupiedRow;
        this.deoccupiedCol = deoccupiedCol;;
    }

    /**
     * @return The row that was occupied.
     */
    public int getDeOccupiedRow() {
        return deoccupiedRow;
    }

    /**
     * @return The column that was occupied.
     */
    public int getDeOccupiedCol() {
        return deoccupiedCol;
    }
}
