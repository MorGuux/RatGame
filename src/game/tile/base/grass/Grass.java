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
     *     <li>[TILE, (GrassSpriteName, int x, int y)]</li>
     * </ul>
     *
     * @param args String in the aforementioned format. Assumes you have
     *             removed all spaces from said string.
     * @return Grass Tile constructed from the provided args.
     */
    public static Grass build(final String args) {
        // Setup regex and relevant groups
        final Pattern p = Pattern.compile(
                "(?i)\\[(.*?),\\[(.*?),([0-9]+),([0-9]+)]]"
        );
        final int spriteNameGroup = 2;
        final int rowGroup = 3;
        final int colGroup = 4;

        // If direct match
        final Matcher m = p.matcher(args);
        if (m.matches()) {
            try {
                final GrassSprite sprite =
                        GrassSprite.valueOf(m.group(spriteNameGroup));

                final int row = Integer.parseInt(m.group(rowGroup));
                final int col = Integer.parseInt(m.group(colGroup));

                return new Grass(sprite, row, col);

                // Enumeration section [A, [B, 0, 0]]; 'B' doesn't exist
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(String.format(
                        "Sprite enumeration Class %s does not exist...",
                        m.group(spriteNameGroup)
                ));
            }

            // String isn't setup correctly
        } else {
            throw new IllegalStateException(String.format(
                    "The provided String [%s] does not meet the expected "
                            + "String [%s]...",
                    args,
                    "[GRASS,[TILE_ENUMERATION,INT,INT]]"
            ));
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
