package game.event.impl.entity.specific.load;

import game.entity.Entity;
import game.event.base.VisualEvent;
import game.event.impl.entity.EntityEvent;

import java.net.URL;

/**
 * Event wraps when an entity is placed into the game.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class EntityLoadEvent extends EntityEvent implements VisualEvent {

    /**
     * Resource attached to the display sprite for the entity.
     */
    private final URL displaySprite;

    /**
     * Time it takes to display the image fully.
     */
    private final int timeFrame;

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author        The target entity.
     * @param displaySprite The image to display for this entity.
     * @param timeFrame     The time to take in order to fully display this
     *                      sprite.
     */
    public EntityLoadEvent(final Entity author,
                           final URL displaySprite,
                           final int timeFrame) {
        super(author);
        this.displaySprite = displaySprite;
        this.timeFrame = timeFrame;
    }

    /**
     * @return The time in milliseconds to take in order to fully display the
     * entity sprite.
     */
    public int getTimeFrame() {
        return timeFrame;
    }

    /**
     * @return Image resource that can be viewed once loaded.
     */
    @Override
    public URL getImageResource() {
        return displaySprite;
    }
}
