package game.tile.base.tunnel;

import game.tile.Tile;
import game.tile.exception.UnknownSpriteEnumeration;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a tunnel Tile for the rat game.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class Tunnel extends Tile {

    /**
     *
     */
    private final TunnelSprite sprite;

    /**
     * Constructs a Tunnel tile from the Sprite type, row and col.
     *
     * @param spriteResource Sprite of the Tunnel.
     * @param initRow Row this Tile exists in, on a Game Map.
     * @param initCol Column this Tile exists in, on a Game Map.
     */
    public Tunnel(final TunnelSprite spriteResource,
                  final int initRow,
                  final int initCol) {
        super(true, spriteResource.getResource(), initRow, initCol);
        this.sprite = spriteResource;
    }

    /**
     * Constructs a Tunnel Tile from the given String args. This only works if
     * the String is properly formatted with the style:
     * <ul>
     *     <li>[TILE, [TunnelSpriteEnumeration, int x, int y]]</li>
     * </ul>
     *
     * @param args String in the aforementioned format.
     * @return Tunnel Tile constructed from the provided args.
     * @see Tile#build(TileFactory, SpriteFactory, String) for exceptions.
     */
    public static Tunnel build(final String args)
            throws UnknownSpriteEnumeration {
        // Error message should provide extra detail
        final SpriteFactory<TunnelSprite> factory = sprite -> {
            try {
                return TunnelSprite.valueOf(sprite);
            } catch (IllegalArgumentException e) {
                throw new UnknownSpriteEnumeration(String.format(
                        ERR_UNKNOWN_SPRITE,
                        args,
                        Tunnel.class.getSimpleName(),
                        sprite,
                        Arrays.deepToString(TunnelSprite.values())
                ));
            }
        };
        Objects.requireNonNull(args);

        return (Tunnel) Tile.build(
                Tunnel::new,
                factory,
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
