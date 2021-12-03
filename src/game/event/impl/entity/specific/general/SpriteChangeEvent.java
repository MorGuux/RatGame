package game.event.impl.entity.specific.general;

import game.entity.Entity;
import game.event.base.VisualEvent;
import game.event.impl.entity.EntityEvent;

import java.net.URL;

/**
 * Wraps an event where an entity goes through some sprite change and is now
 * of a different sprite.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class SpriteChangeEvent extends EntityEvent implements VisualEvent {

    /**
     * The time it takes to transition into the new sprite.
     */
    private final int timeFrame;

    /**
     * The new sprite to transition into.
     */
    private final URL displaySprite;

    /**
     * The rotation of the image that is to be displayed.
     */
    private final int imageRotation;

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author        The target entity.
     * @param displaySprite The new sprite to display for the target entity.
     * @param timeFrame     The to take in order to fully transition into the
     *                      new sprite.
     */
    public SpriteChangeEvent(final Entity author,
                             final int timeFrame,
                             final URL displaySprite) {
        super(author);
        this.timeFrame = timeFrame;
        this.displaySprite = displaySprite;
        this.imageRotation = 0;
    }

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author        The target entity.
     * @param displaySprite The new sprite to display for the target entity.
     * @param imageRotation The rotation of the image that is to be displayed.
     * @param timeFrame     The to take in order to fully transition into the
     *                      new sprite.
     */
    public SpriteChangeEvent(final Entity author,
                             final int timeFrame,
                             final int imageRotation,
                             final URL displaySprite) {
        super(author);
        this.timeFrame = timeFrame;
        this.displaySprite = displaySprite;
        this.imageRotation = imageRotation;
    }

    /**
     * @return Image resource that can be viewed once loaded.
     */
    @Override
    public URL getImageResource() {
        return displaySprite;
    }

    /**
     * @return The time in milliseconds in order to fully showcase this
     * sprite change.
     */
    public int getTimeFrame() {
        return timeFrame;
    }

    /**
     * @return The rotation in degrees that the image should be.
     */
    public int getImageRotation() {
        return imageRotation;
    }
}
