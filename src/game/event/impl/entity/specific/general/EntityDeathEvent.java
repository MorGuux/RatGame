package game.event.impl.entity.specific.general;

import game.entity.Entity;
import game.event.base.AudioEvent;
import game.event.base.VisualEvent;
import game.event.impl.entity.EntityEvent;

import java.net.URL;

/**
 * Event wraps when an entity has died and what should be displayed
 * visually, and audibly.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class EntityDeathEvent extends EntityEvent
        implements VisualEvent, AudioEvent {

    /**
     * The image to show when this entity dies.
     */
    private final URL displaySprite;

    /**
     * The sound clip to play when the entity dies.
     */
    private final URL audioClip;

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author        The target entity.
     * @param displaySprite The sprite to display on the origin tile.
     * @param audioClip     The audio clip to play when the entity dies.
     */
    public EntityDeathEvent(final Entity author,
                            final URL displaySprite,
                            final URL audioClip) {
        super(author);
        this.displaySprite = displaySprite;
        this.audioClip = audioClip;
    }

    /**
     * @return Resource attached to an audio clip that should be played
     * when this event happens.
     */
    @Override
    public URL getAudioClip() {
        return audioClip;
    }

    /**
     * @return Image resource that can be viewed once loaded.
     */
    @Override
    public URL getImageResource() {
        return displaySprite;
    }
}
