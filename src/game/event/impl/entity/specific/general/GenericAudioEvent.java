package game.event.impl.entity.specific.general;

import game.entity.Entity;
import game.event.base.AudioEvent;
import game.event.impl.entity.EntityEvent;

import java.net.URL;

/**
 * Generic audio event for getting a piece of audio sent to the listener.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class GenericAudioEvent extends EntityEvent implements AudioEvent {

    /**
     * The audio clip for this event.
     */
    private final URL audioClip;

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author The target entity.
     * @param audioSource The audio clip to be played.
     */
    public GenericAudioEvent(final Entity author,
                             final URL audioSource) {
        super(author);
        this.audioClip = audioSource;
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
