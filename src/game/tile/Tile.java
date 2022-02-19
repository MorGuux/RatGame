package game.tile;

import game.tile.exception.UnknownSpriteEnumeration;
import game.tile.loader.TileLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.regex.Matcher;

/**
 * Tile wraps the underlying objects that create a Game Map encapsulating all
 * the required data and shared attributes that every Tile will require.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public abstract class Tile {

    /**
     * Error message for when loading from a string and the Sprite type is
     * unknown or incorrectly typed.
     */
    protected static final String ERR_UNKNOWN_SPRITE = "Could not load from "
            + "\"%s\" the Tile type \"%s\" as the Sprite class \"%s\" could "
            + "not be directly matched to any of the acceptable sprites \"%s\"."
            + "..";

    /**
     * Error message for when loading from a string and the String itself
     * isn't setup correctly.
     */
    protected static final String ERR_ARGS_MALFORMED = "The provided args "
            + "\"%s\" are not properly formatted into the expected: [A[B,0,0]]";

    /**
     * Tile factory interface used to create tile objects from the given
     * parameters.
     *
     * @param <T> Sprite image resource type.
     */
    public interface TileFactory<T> {
        Tile create(T t, int row, int col);
    }

    /**
     * @param <T> Sprite image resource type.
     */
    protected interface SpriteFactory<T> {
        T create(String sprite)
                throws UnknownSpriteEnumeration;
    }

    /**
     * Attempts to create a Tile from the provided arguments. This assumes a
     * single format for String args:
     *
     * <ul>
     *     <li>[TILE_CLASS, [SPRITE_CLASS, INT, INT]]</li>
     * </ul>
     * <p>
     * If it matches generically it's called a Soft match, that being that it
     * is of the format but may not necessarily be a valid args string. For
     * instance the Sprite class or Tile class may not actually exist.
     *
     * @param tileSupply     Create from the successful args the Tile the args
     *                       refer to.
     * @param spriteSupplier Create the Sprite image required to create the
     *                       Tile.
     * @param args           String args to load.
     * @param <T>            Sprite enumeration type.
     * @return Newly constructed Tile using the provided Args.
     * @throws IllegalArgumentException If the provided args don't pass as a
     *                                  soft match for String ->
     *                                  Tile conversion.
     * @throws UnknownSpriteEnumeration If the String args are a soft match
     *                                  but don't actually refer to any known
     *                                  sprite enumeration implementation.
     */
    public static <T> Tile build(final TileFactory<T> tileSupply,
                                 final SpriteFactory<T> spriteSupplier,
                                 final String args)
            throws UnknownSpriteEnumeration {
        final int spriteName = 2;
        final int rowGroup = 3;
        final int colGroup = 4;

        // If direct match
        final Matcher m = TileLoader.SOFT_MATCH_REGEX.matcher(args);
        if (m.matches()) {
            final T sprite = spriteSupplier.create(m.group(spriteName));
            final int row = Integer.parseInt(m.group(rowGroup));
            final int col = Integer.parseInt(m.group(colGroup));

            return tileSupply.create(sprite, row, col);

            // String isn't setup correctly
        } else {
            throw new IllegalArgumentException(String.format(
                    ERR_ARGS_MALFORMED,
                    args
            ));
        }
    }

    /**
     * Default size for all tiles, where the Tiles are square.
     */
    public static final int DEFAULT_SIZE = 48;

    /**
     * The Row this Tile resides on; in a 2D Array.
     */
    private final int row;

    /**
     * The Col this Tile resides on; in a 2D Array.
     */
    private final int col;

    /**
     * States whether an Entity can interact with this tile. By either
     * standing on it or having some form of state transition of the Tile.
     */
    private final boolean canInteract;

    /**
     * Image Sprite for this Tile.
     */
    private final URL resource;

    /**
     * Constructs a Tile from the interaction state and a given Sprite resource.
     *
     * @param canInteract    States if an Entity should be able to interact with
     *                       this Tile or not.
     * @param spriteResource Resource of the Tiles visual sprite that should
     *                       be displayed.
     * @param initRow        Row this Tile exists in, on a Game Map.
     * @param initCol        Column this Tile exists in, on a Game Map.
     */
    public Tile(final boolean canInteract,
                final URL spriteResource,
                final int initRow,
                final int initCol) {
        this.canInteract = canInteract;
        this.resource = spriteResource;
        this.row = initRow;
        this.col = initCol;
    }

    /**
     * @return Row this Tile exists in on a Game Map.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return Column this Tile exists on in a Game Map.
     */
    public int getCol() {
        return col;
    }

    /**
     * @return Sprite image resource provided at construction.
     */
    protected URL getResource() {
        return resource;
    }

    /**
     * States if an Entity can interact with this Tile or not. Where
     * interactions are of:
     * <ul>
     *     <li>Entity can stand on this tile.</li>
     *     <li>Entity can do something on this tile.</li>
     *     <li>Entity can do something to this tile.</li>
     * </ul>
     *
     * @return {@code true} if an Entity can interact with this Tile.
     * Otherwise, if an Entity cannot interact with this Tile then {@code
     * false} is returned.
     */
    public boolean isCanInteract() {
        return canInteract;
    }

    /**
     * Gets a JavaFX implementation of the Tile ready to be displayed on a
     * Scene Graph.
     *
     * @return Graphical representation of this Tile using the Default build
     * parameters.
     */
    protected ImageView getDefaultImageView() {
        final Image i = new Image(resource.toExternalForm());
        final ImageView view = new ImageView(i);

        view.setPreserveRatio(true);
        view.setSmooth(false);
        view.setFitHeight(DEFAULT_SIZE);
        view.setFitHeight(DEFAULT_SIZE);

        return view;
    }

    /**
     * @return JavaFX Node of 'this' Tile ready to be displayed on a Scene
     * Graph.
     */
    public abstract ImageView getFXSpriteView();

    /**
     * Build this Tile to a String that can be saved to a File.
     *
     * @return Args required to build the 'this' tile.
     */
    public abstract String buildToString();
}
