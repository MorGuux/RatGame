package game.tile;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

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
     * Default size for all tiles, where the Tiles are square.
     */
    protected static final int DEFAULT_SIZE = 64;

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

        view.setPreserveRatio(false);
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
