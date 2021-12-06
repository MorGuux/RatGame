package game.contextmap.handler.result;

import game.contextmap.TileData;
import game.entity.Entity;
import java.util.Objects;

/**
 * Represents a potential move that an Entity can make.
 */
public class MovementResult {

    /**
     * The position we moved from.
     */
    private final TileData fromPosition;

    /**
     * The position we are trying to go to.
     */
    private final TileData toPosition;

    /**
     * The success state of the movement.
     */
    private final boolean wasBlocked;

    /**
     * The entities if any that blocked the move.
     */
    private final Entity[] entitiesThatBlocked;

    /**
     * Constructs a MovementResult for a failed move where the movement could
     * not be done due to an Entity/Entities that blocked the move.
     *
     * @param fromPosition The previous position.
     * @param toPosition   The position that was attempted to be moved
     *                     to.
     * @param blockedBy    Entities that blocked the move.
     * @throws NullPointerException If any parameters are null.
     */
    public MovementResult(final TileData fromPosition,
                          final TileData toPosition,
                          final Entity... blockedBy) {
        Objects.requireNonNull(fromPosition);
        Objects.requireNonNull(toPosition);
        Objects.requireNonNull(blockedBy);

        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.entitiesThatBlocked = blockedBy;
        this.wasBlocked = blockedBy.length != 0;
    }

    /**
     * @return {@code true} if the move was blocked by some entities.
     * Otherwise, if not then {@code false}.
     */
    public boolean wasBlocked() {
        return wasBlocked;
    }

    /**
     * Gets all the Entities that blocked the move even if there were no
     * Entities that blocked the move.
     *
     * @return All Entities if any that blocked the move.
     */
    public Entity[] getEntitiesThatBlocked() {
        return entitiesThatBlocked;
    }

    /**
     * Get the position that we are moving from 0,0 to 0,1 (0,0 is from).
     * <p>
     * For both cases of a Successful and Failed move this will always be the
     * same value.
     *
     * @return The position we moved from.
     */
    public TileData getFromPosition() {
        return fromPosition;
    }

    /**
     * Get the Entities that exist on the {@code from position}.
     * <p>
     * This method is equivalent to: {@code getFromPosition().getEntities();}
     * there are no differences or extra checks.
     *
     * @return All entities on {@link #fromPosition}.
     */
    public Entity[] getEntitiesOnFromPos() {
        return fromPosition.getEntities();
    }

    /**
     * Get the position that was moved to: 0,0 to 0,1 (0,1 is to).
     * <p>
     * For both cases of a Successful and Failed move this will always exist
     * just the context should be considered different.
     *
     * @return The position we are/tried moving to.
     */
    public TileData getToPosition() {
        return toPosition;
    }

    /**
     * All entities that reside on the {@link #getToPosition()}; the function
     * is equivalent to {@code getToPosition().getEntities();}.
     *
     * @return All entities that are on the "To position".
     * @see #getEntitiesOnFromPos()
     */
    public Entity[] getEntitiesOnToPos() {
        return toPosition.getEntities();
    }
}
