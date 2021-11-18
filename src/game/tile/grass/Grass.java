package game.tile.grass;

import game.tile.Tile;
import javafx.scene.image.ImageView;

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
     * @param grassSprite Grass Sprite for this Grass Tile.
     * @param row    Row this Tile should exist on in a Game Map.
     * @param col    Column this Tile should exist on in a Game Map.
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
     *     <li>[TILE, (GrassSpriteName, int x, int y)]</li>
     * </ul>
     * @param args String in the aforementioned format.
     * @return Grass Tile constructed from the provided args.
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
            try {
                final GrassSprite sprite =
                        GrassSprite.valueOf(m.group(spriteNameGroup));

                final int row = Integer.parseInt(m.group(rowGroup));
                final int col = Integer.parseInt(m.group(colGroup));

                return new Grass(sprite, row, col);

            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(String.format(
                        "Sprite enumeration Class %s does not exist...",
                        m.group(spriteNameGroup)
                ));
            }
        } else {
            throw new IllegalStateException(
                    "Illegally formatted String: "
                            + args
            );
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

    /**
     * Build this Tile to a String that can be saved to a File.
     *
     * @return Args required to build the 'this' tile.
     */
    @Override
    public String buildToString() {
        String base = "[GRASS, (%s, %s, %s)]";
        return String.format(base, sprite.name(), getRow(), getCol());
    }
}
