package game.event.impl.entity.specific.general;

import game.entity.Entity;
import game.event.base.AudioEvent;
import game.event.base.VisualEvent;
import game.event.impl.entity.EntityEvent;
import game.tile.Tile;

import java.net.URL;

/**
 * Event wraps when an Entity occupies a tile.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public class EntityOccupyTileEvent extends EntityEvent
        implements VisualEvent, AudioEvent {

    /**
     * The row that was occupied.
     */
    private final int occupiedRow;

    /**
     * The column that was occupied.
     */
    private final int occupiedCol;

    /**
     * The time it should take to fully occupy the target tile visually.
     */
    private final int timeFrame;

    /**
     * The sprite to display visually while occupying the tile.
     */
    private final URL displaySprite;

    /**
     * The audio to play when this event is handled.
     */
    private final URL audioClip;

    /**
     * The size of the image to display.
     */
    private final int imageSize;

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author        The target entity.
     * @param occupiedRowPosition   The row that was occupied.
     * @param occupiedColPosition   The column that was occupied.
     * @param occupyTimeFrame     The time in milliseconds it should take to
     *                            occupy the tile.
     * @param occupyDisplaySprite The image to display when this event occurs.
     * @param occupyAudioClip     The audio to display when this event occurs.
     */
    public EntityOccupyTileEvent(final Entity author,
                                 final int occupiedRowPosition,
                                 final int occupiedColPosition,
                                 final int occupyTimeFrame,
                                 final URL occupyDisplaySprite,
                                 final URL occupyAudioClip) {
        super(author);
        this.occupiedRow = occupiedRowPosition;
        this.occupiedCol = occupiedColPosition;
        this.timeFrame = occupyTimeFrame;
        this.displaySprite = occupyDisplaySprite;
        this.audioClip = occupyAudioClip;
        this.imageSize = Tile.DEFAULT_SIZE;
    }

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author        The target entity.
     * @param occupiedRowPosition   The row that was occupied.
     * @param occupiedColPosition   The column that was occupied.
     * @param occupyTimeFrame     The time in milliseconds it should take to
     *                            occupy the tile.
     * @param occupyDisplaySprite The image to display when this event occurs.
     * @param occupyAudioClip     The audio to display when this event occurs.
     * @param occupyImageSize    The size to the display the image at.
     */
    public EntityOccupyTileEvent(final Entity author,
                                 final int occupiedRowPosition,
                                 final int occupiedColPosition,
                                 final int occupyTimeFrame,
                                 final URL occupyDisplaySprite,
                                 final URL occupyAudioClip,
                                 final int occupyImageSize) {
        super(author);
        this.occupiedRow = occupiedRowPosition;
        this.occupiedCol = occupiedColPosition;
        this.timeFrame = occupyTimeFrame;
        this.displaySprite = occupyDisplaySprite;
        this.audioClip = occupyAudioClip;
        this.imageSize = occupyImageSize;
    }

    /**
     * @return The row that was occupied.
     */
    public int getOccupiedRow() {
        return occupiedRow;
    }

    /**
     * @return The column that was occupied.
     */
    public int getOccupiedCol() {
        return occupiedCol;
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

    /**
     * @return The size that the image should be.
     */
    public int getImageSize() {
        return imageSize;
    }
}
