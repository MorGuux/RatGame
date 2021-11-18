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
    DEAD_END(getResource("GrassDeadEnd.png"), 0),

    // todo T_Junction needs to be edited to default towards north; also need
    //  to add in the rotational junctions.
    T_JUNCTION(getResource("GrassTJunction.png"), 0),
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
     * @param resource Sprite image resource location.
     * @param rotation Rotation to apply to the Sprite in degrees to get the
     *                 Sprite oriented correctly.
     * @throws NullPointerException If the resource is {@code null}.
     */
    GrassSprite(final URL resource,
                final int rotation) {
        Objects.requireNonNull(resource);
        this.resource = resource;
        this.rotation = rotation;
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
