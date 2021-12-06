package game.tile.base.path;

import game.tile.SpriteResource;

import java.net.URL;

/**
 * Path sprite represents all the regular rat game Path sprites that can be
 * stood on.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public enum PathSprite implements SpriteResource {
    /**
     * A bare path with no direction.
     */
    BARE_PATH(PathSprite.class.getResource("assets/Bare_Path.png"), 0);

    /**
     * Resource, not null.
     */
    private final URL resource;

    /**
     * Rotation in degrees such as 90 or 180.
     */
    private final int rotation;

    /**
     * Constructs the sprite resource from the base Tunnel sprite resource
     * and its required rotation.
     *
     * @param resource Image url location.
     * @param rotation Rotation in degrees.
     */
    PathSprite(final URL resource,
               final int rotation) {
        this.resource = resource;
        this.rotation = rotation;
    }

    /**
     * Get the URL Resource of this sprite resource.
     *
     * @return URL Attached to the Sprite.
     */
    @Override
    public URL getResource() {
        return resource;
    }

    /**
     * @return Rotation in degrees required to get the sprite resource
     * oriented correctly.
     */
    @Override
    public int getRotation() {
        return rotation;
    }
}
