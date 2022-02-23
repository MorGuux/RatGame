package game.tile.loader;

import game.tile.SpriteResource;
import game.tile.Tile;
import game.tile.base.grass.Grass;
import game.tile.base.grass.GrassSprite;
import game.tile.base.path.Path;
import game.tile.base.path.PathSprite;
import game.tile.base.tunnel.Tunnel;
import game.tile.base.tunnel.TunnelSprite;
import game.tile.exception.UnknownSpriteEnumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Tile regex wraps the three main Tile types and produces a regex for each
 * that wraps the all the possible combinations for the tile to be loaded
 * from a string.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public enum TileRegex {

    // todo I replaced the Enum<?>[] with SpriteResource[] since we had that.
    //  Though i don't know if it will work (might need to double check it)

    /**
     * Wraps a Game Path tile object.
     */
    PATH(PathSprite.values(), Path::build, Path.class, Path::new),

    /**
     * Wraps a Game Grass tile object.
     */
    GRASS(GrassSprite.values(), Grass::build, Grass.class, Grass::new),

    /**
     * Wraps a Game Tunnel object.
     */
    TUNNEL(TunnelSprite.values(), Tunnel::build, Tunnel.class, Tunnel::new);

    /**
     * Inner builder used to wrap Tile supplier methods for any tile type.
     */
    private interface Builder {
        Tile build(String raw) throws UnknownSpriteEnumeration;
    }

    /**
     * Regex that matches wholly in format: [A,[B,0,0]] note the spaces.
     * Where b is a direct matching of one of the values (case-insensitive)
     * found in the Sprite enumerations.
     */
    private final Pattern regex;

    /**
     * Tile loader factory method.
     */
    private final Builder loader;

    /**
     * The tile class type of the target tile regex.
     */
    private final Class<? extends Tile> targetClass;

    /**
     * Factory object used to create new instances of the target tile.
     */
    private final Tile.TileFactory<SpriteResource> tileFactory;

    /**
     * Sprites that the tile can use.
     */
    private final SpriteResource[] availableSprites;

    /**
     * Default enum constructor.
     *
     * @param tileSprites Tile sprite enumeration set. Such as
     *                    {@link PathSprite} which is the set for Path tiles.
     * @param builder     Builder method to go from String args -> Tile
     *                    instance.
     * @param targetTile  The target tile class type.
     * @param factory     Factory instance which can construct new instances of
     *                    the target tile.
     */
    TileRegex(final SpriteResource[] tileSprites,
              final Builder builder,
              final Class<? extends Tile> targetTile,
              final Tile.TileFactory<SpriteResource> factory) {
        final String s = "(?im)\\[%s,\\[%s,[0-9]+,[0-9]+]]";

        regex = Pattern.compile(String.format(
                s, name(), parseSpriteSet(tileSprites)
        ));

        this.loader = builder;
        this.targetClass = targetTile;
        this.tileFactory = factory;
        this.availableSprites = tileSprites;
    }

    /**
     * Parses the name of all the TileSprite enumerations for the Tile into a
     * single regex string.
     *
     * @param set Possible sprite names.
     * @return Regex string encapsulated in its own group.
     */
    private String parseSpriteSet(final SpriteResource[] set) {
        // Acceptable inputs are A or B or C. aka (A|B|C)
        final String separator = "|";
        final List<String> names = new ArrayList<>();
        Arrays.stream(set).forEach(i -> names.add(i.name()));

        return String.format("(%s)", String.join(separator, names));
    }

    /**
     * Checks to see if it is completely possible that the provided String
     * has enough arguments to be parsed into the respective Tile.
     *
     * @param s args to compile.
     * @return {@code true} if the
     */
    public boolean canAssertThis(final String s) {
        Objects.requireNonNull(s);
        return regex.matcher(s).matches();
    }

    /**
     * Builds the tile as if it was the target type.
     *
     * @param raw Args string to parse.
     * @return Tile castable to 'this'.
     */
    public Tile build(final String raw) throws UnknownSpriteEnumeration {
        return loader.build(raw);
    }

    /**
     * @return Target class type of this tile.
     */
    public Class<? extends Tile> getTargetClass() {
        return targetClass;
    }

    /**
     * @return Tile factory which can create new instances of the target tile.
     */
    public Tile.TileFactory<SpriteResource> getTileFactory() {
        return tileFactory;
    }

    /**
     * @return Array of all possible sprite resources.
     */
    public SpriteResource[] getAvailableSprites() {
        return availableSprites;
    }
}
