package game.tile.base.grass;

import game.tile.SpriteResource;

import java.net.URL;
import java.util.Objects;

/**
 * Enumerates the Grass Sprite types available for the Grass Tile.
 *
 * @author -Ry, Maksim
 * @version 0.1
 * Copyright: N/A
 */
public enum GrassSprite implements SpriteResource {
    BARE_GRASS(getResource("Bare_Grass.png"), 0);

    /**
     * Base Grass Sprite resource.
     */
    private final URL resource;

    /**
     * Rotation needed for the Grass Sprite.
     */
    private final int rotation;

    /**
     * @param url         Sprite image resource location.
     * @param orientation Rotation to apply to the Sprite in degrees to get the
     *                    Sprite oriented correctly.
     * @throws NullPointerException If the resource is {@code null}.
     */
    GrassSprite(final URL url,
                final int orientation) {
        Objects.requireNonNull(url);
        this.resource = url;
        this.rotation = orientation;
    }

    /**
     * @return Sprite image resource.
     */
    public URL getResource() {
        return resource;
    }

    /**
     * @return Rotation needed to be applied to the Sprite image resource.
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Convenience method for obtaining Grass Sprites without qualifying
     * everything.
     *
     * @param name Name of the resource to get.
     * @return URL attached to the provided resource or {@code null} if no
     * resource could be loaded.
     */
    private static URL getResource(final String name) {
        return GrassSprite.class.getResource("assets/" + name);
    }
}
