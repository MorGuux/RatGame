package game.tile;

import java.net.URL;

/**
 * Sprite Resource encapsulates the common methods used for the Tile Sprite
 * containers for things such as getting a resource and rotation of the
 * resource.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface SpriteResource {

    /**
     * Get the URL Resource of this sprite resource.
     *
     * @return URL Attached to the Sprite.
     */
    URL getResource();

    /**
     * @return Rotation in degrees required to get the sprite resource
     * oriented correctly.
     */
    int getRotation();
}
