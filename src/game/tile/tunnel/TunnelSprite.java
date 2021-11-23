package game.tile.tunnel;

import game.tile.SpriteResource;

import java.net.URL;

/**
 * Tunnel Sprite wraps all the literal images available for the Tile, Tunnel.
 * And its required rotation.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public enum TunnelSprite implements SpriteResource {
    ;
    // todo finish

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
    TunnelSprite(final URL resource,
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
