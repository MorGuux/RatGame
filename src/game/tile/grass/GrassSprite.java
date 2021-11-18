package game.tile.grass;

import java.net.URL;
import java.util.Objects;

/**
 * Enumerates the Grass Sprite types available for the Grass Tile.
 *
 * @author -Ry, Maksim
 * @version 0.1
 * Copyright: N/A
 */
public enum GrassSprite {
    BARE(getResource("GrassBare.png"), 0),
    CROSS_ROAD(getResource("GrassCrossroad.png"), 0),

    DEAD_END_F(getResource("GrassDeadEnd.png"), 0),
    DEAD_END_R(getResource("GrassDeadEnd.png"), 90),
    DEAD_END_B(getResource("GrassDeadEnd.png"), 180),
    DEAD_END_L(getResource("GrassDeadEnd.png"), 270),

    T_JUNCTION_LR(getResource("GrassTJunction.png"), 0),
    T_JUNCTION_LF(getResource("GrassTJunction.png"), 90),
    T_JUNCTION_BLR(getResource("GrassTJunction.png"), 180),
    T_JUNCTION_RF(getResource("GrassTJunction.png"), 270),

    TURN_F_RIGHT(getResource("GrassTurnR.png"), 0),
    TURN_F_LEFT(getResource("GrassTurnR.png"), 90),
    TURN_B_LEFT(getResource("GrassTurnR.png"), 180),
    TURN_B_RIGHT(getResource("GrassTurnR.png"), 270),

    VERTICAL(getResource("GrassVertical.png"), 0),
    HORIZONTAL(getResource("GrassVertical.png"), 90);

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
