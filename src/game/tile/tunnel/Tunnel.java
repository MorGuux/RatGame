package game.tile.tunnel;

import game.tile.Tile;
import javafx.scene.image.ImageView;

/**
 * Represents a tunnel Tile for the rat game.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class Tunnel extends Tile {

    /**
     * Constructs a Tunnel tile from the Sprite type, row and col.
     *
     * @param initRow Row this Tile exists in, on a Game Map.
     * @param initCol Column this Tile exists in, on a Game Map.
     */
    public Tunnel(final TunnelSprite spriteResource,
                  final int initRow,
                  final int initCol) {
        super(true, spriteResource.getResource(), initRow, initCol);
    }

    /**
     * @return JavaFX Node of 'this' Tile ready to be displayed on a Scene
     * Graph.
     */
    @Override
    public ImageView getFXSpriteView() {
        return null;
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
