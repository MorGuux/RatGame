package game.tile.grass;

import game.tile.Tile;
import javafx.scene.image.ImageView;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @param sprite Grass Sprite for this Grass Tile.
     * @param row    Row this Tile should exist on in a Game Map.
     * @param col    Column this Tile should exist on in a Game Map.
     */
    public Grass(final GrassSprite sprite,
                 final int row,
                 final int col) {
        super(false, sprite.getResource(), row, col);
        this.sprite = sprite;
    }

    /**
     * Constructs a Grass Tile from the given String args. This only works if
     * the String is properly formatted with the style:
     * <ul>
     *     <li>[TILE, (GrassSpriteName, int x, int y)]</li>
     * </ul>
     *
     */
    public static Grass build(final String args) {
        // todo this is temporary. We probably want a single Static Class for
        //  these. Or perhaps keeping them relevant to their classes is best.

        // We remove all spaces and new lines since they don't matter
        final String base = args.replaceAll("\s", "");
        final Pattern p = Pattern.compile(
                "\\[(?i)GRASS,\\((.*),([0-9]+),([0-9]+)\\)]"
        );
        final int spriteNameGroup = 1;
        final int rowGroup = 2;
        final int colGroup = 3;

        final Matcher m = p.matcher(base);
        if (m.matches()) {
            final GrassSprite sprite =
                    GrassSprite.valueOf(m.group(spriteNameGroup));

            final int row = Integer.parseInt(m.group(rowGroup));
            final int col = Integer.parseInt(m.group(colGroup));

            return new Grass(sprite, row, col);
        } else {
            throw new IllegalStateException("Illegally formatted String: " + args);
        }
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
}
