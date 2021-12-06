package game.tile.base.tunnel;

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
    /**
     * Vertical tunnel sprite.
     */
    VERTICAL(loadResource("Tunnel_Vertical.png"), 0),
    /**
     * Horizontal tunnel sprite.
     */
    HORIZONTAL(loadResource("Tunnel_Vertical.png"), 90),

    /**
     * 4-way intersection of a tunnel sprite.
     */
    CROSS_ROAD(loadResource("Tunnel_Crossroad.png"), 0),

    /**
     * Front right turn of a tunnel sprite.
     */
    TURN_F_RIGHT(loadResource("Tunnel_Turn_Right.png"), 0),
    /**
     * Front left turn of a tunnel sprite.
     */
    TURN_F_LEFT(loadResource("Tunnel_Turn_Right.png"), 90),
    /**
     * Back left turn of a tunnel sprite.
     */
    TURN_B_LEFT(loadResource("Tunnel_Turn_Right.png"), 180),
    /**
     * Back right turn of a tunnel sprite.
     */
    TURN_B_RIGHT(loadResource("Tunnel_Turn_Right.png"), 270);

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
     * Convenience method to get a resource from the default assets folder.
     *
     * @param name Name of the asset.
     * @return URL attached to the asset if it could be found. Else, null.
     */
    private static URL loadResource(final String name) {
        return TunnelSprite.class.getResource("assets/" + name);
    }

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
