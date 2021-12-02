package game.event.impl.entity.specific.general;

import game.entity.Entity;
import game.event.base.AudioEvent;
import game.event.base.VisualEvent;
import game.event.impl.entity.EntityEvent;

import java.net.URL;

/**
 * Event wraps when an Entity de-occupies a tile.
 *
 * @author Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */
public class EntityDeOccupyTileEvent extends EntityEvent
        implements VisualEvent, AudioEvent {

    /**
     * The row that was de-occupied.
     */
    private final int deoccupiedRow;

    /**
     * The column that was de-occupied.
     */
    private final int deoccupiedCol;

    /**
     * The time it should take to fully de-occupy the target tile visually.
     */
    private final int timeFrame;

    /**
     * The sprite to display visually while de-occupying the tile.
     */
    private final URL displaySprite;

    /**
     * The audio to play when this event is handled.
     */
    private final URL audioClip;

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author        The target entity.
     * @param deoccupiedRow   The row that was de-occupied.
     * @param deoccupiedCol   The column that was de-occupied.
     * @param timeFrame     The time in milliseconds it should take to de-occupy
     *                      the tile.
     * @param displaySprite The image to display when this event occurs.
     * @param audioClip     The audio to display when this event occurs.
     */
    public EntityDeOccupyTileEvent(final Entity author,
                                   final int deoccupiedRow,
                                   final int deoccupiedCol,
                                   final int timeFrame,
                                   final URL displaySprite,
                                   final URL audioClip) {
        super(author);
        this.deoccupiedRow = deoccupiedRow;
        this.deoccupiedCol = deoccupiedCol;
        this.timeFrame = timeFrame;
        this.displaySprite = displaySprite;
        this.audioClip = audioClip;
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

    /**
     * @return The time taken to fully occupy the tile.
     */
    public int getTimeFrame() {
        return timeFrame;
    }

    /**
     * @return {@code true} if this event has a display sprite whilst
     * occupying.
     */
    public boolean hasDisplaySprite() {
        return getImageResource() != null;
    }

    /**
     * @return Image resource that can be viewed once loaded.
     */
    @Override
    public URL getImageResource() {
        return this.displaySprite;
    }

    /**
     * @return {@code true} if this event has an audio target to play when it
     * occurs.
     */
    public boolean hasAudioClip() {
        return getAudioClip() != null;
    }

    /**
     * @return Resource attached to an audio clip that should be played
     * when this event happens.
     */
    @Override
    public URL getAudioClip() {
        return this.audioClip;
    }
}
