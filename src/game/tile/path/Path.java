package game.tile.path;

import game.tile.Tile;
import javafx.scene.image.ImageView;


/**
 * Path represents a tile that can be stood on by any Entity or Item.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class Path extends Tile {

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
