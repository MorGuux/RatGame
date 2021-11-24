package game.tile.base.grass;

import game.tile.Tile;
import game.tile.base.tunnel.Tunnel;
import game.tile.base.tunnel.TunnelSprite;
import game.tile.exception.UnknownSpriteEnumeration;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.Objects;

/**
 * Encapsulates generically all Grass Sprite types.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class Grass extends Tile {

    /**
     * Grass sprite resource bundle.
     */
    private final GrassSprite sprite;

    /**
     * Constructs a Grass Tile from a Base Grass Sprite.
     *
     * @param grassSprite Grass Sprite for this Grass Tile.
     * @param row         Row this Tile should exist on in a Game Map.
     * @param col         Column this Tile should exist on in a Game Map.
     */
    public Grass(final GrassSprite grassSprite,
                 final int row,
                 final int col) {
        super(false, grassSprite.getResource(), row, col);
        this.sprite = grassSprite;
    }

    /**
     * Constructs a Grass Tile from the given String args. This only works if
     * the String is properly formatted with the style:
     * <ul>
     *     <li>[TILE, [GrassSpriteEnumeration, int x, int y]]</li>
     * </ul>
     *
     * @param args String in the aforementioned format.
     * @return Grass Tile constructed from the provided args.
     * @see Tile#build(TileFactory, SpriteFactory, String) for exceptions.
     */
    public static Grass build(final String args)
            throws UnknownSpriteEnumeration {
        // Error message should provide extra detail
        final SpriteFactory<GrassSprite> spriteFactory = sprite -> {
            try {
                return GrassSprite.valueOf(sprite);
            } catch (IllegalArgumentException e) {
                throw new UnknownSpriteEnumeration(String.format(
                        ERR_UNKNOWN_SPRITE,
                        args,
                        Grass.class.getSimpleName(),
                        sprite,
                        Arrays.deepToString(GrassSprite.values())
                ));
            }
        };
        Objects.requireNonNull(args);

        return (Grass) build(
                Grass::new,
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
        String base = "[GRASS, [%s, %s, %s]]";
        return String.format(base, sprite.name(), getRow(), getCol());
    }
}
