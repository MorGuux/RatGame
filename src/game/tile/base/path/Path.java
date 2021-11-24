package game.tile.base.path;

import game.tile.Tile;
import game.tile.exception.UnknownSpriteEnumeration;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.Objects;


/**
 * Path represents a tile that can be stood on by any Entity or Item.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class Path extends Tile {

    /**
     *
     */
    private final PathSprite sprite;

    /**
     * Constructs a Tile from the interaction state and a given Sprite resource.
     *
     * @param spriteResource Resource of the Tiles visual sprite that should
     *                       be displayed.
     * @param initRow        Row this Tile exists in, on a Game Map.
     * @param initCol        Column this Tile exists in, on a Game Map.
     */
    public Path(final PathSprite spriteResource,
                final int initRow,
                final int initCol) {
        super(true, spriteResource.getResource(), initRow, initCol);
        this.sprite = spriteResource;
    }

    /**
     * Constructs a Path Tile from the given String args. This only works if
     * the String is properly formatted with the style:
     * <ul>
     *     <li>[TILE, [PathSpriteEnumeration, int x, int y]]</li>
     * </ul>
     *
     * @param args String in the aforementioned format.
     * @return Path Tile constructed from the provided args.
     * @see Tile#build(TileFactory, SpriteFactory, String) for exceptions.
     */
    public static Path build(final String args)
            throws UnknownSpriteEnumeration {
        // Error message should provide extra detail
        final SpriteFactory<PathSprite> spriteFactory = sprite -> {
            try {
                return PathSprite.valueOf(sprite);
            } catch (IllegalArgumentException e) {
                throw new UnknownSpriteEnumeration(String.format(
                        ERR_UNKNOWN_SPRITE,
                        args,
                        Path.class.getSimpleName(),
                        sprite,
                        Arrays.deepToString(PathSprite.values())
                ));
            }
        };
        Objects.requireNonNull(args);

        return (Path) build(
                Path::new,
                spriteFactory,
                args.replaceAll("\\s", "")
        );
    }

    /**
     * @return JavaFX Node of 'this' Tile ready to be displayed on a Scene
     * Graph.
     */
    @Override
    public ImageView getFXSpriteView() {
        final ImageView view = this.getDefaultImageView();
        view.rotateProperty().set(sprite.getRotation());

        return view;
    }

    /**
     * Build this Tile to a String that can be saved to a File.
     *
     * @return Args required to build the 'this' tile.
     */
    @Override
    public String buildToString() {
        return null;
    }
}
