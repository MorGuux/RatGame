package game.event.base;

import java.net.URL;

/**
 * Wraps when an Event has some audio to generate.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface AudioEvent {

    /**
     * @return Resource attached to an audio clip that should be played
     * when this event happens.
     */
    URL getAudioClip();
}
