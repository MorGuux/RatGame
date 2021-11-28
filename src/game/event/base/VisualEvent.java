package game.event.base;

import java.net.URL;

/**
 * Wraps an event that will produce a visual target such as OnEntitySpawned
 * and OnEntityOccupy.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface VisualEvent {

    /**
     * @return Image resource that can be viewed once loaded.
     */
    URL getImageResource();
}
