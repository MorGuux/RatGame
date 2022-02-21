package gui.editor.module.tile.single;

import game.tile.SpriteResource;
import game.tile.Tile;
import gui.editor.module.dependant.CustomEventDataMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2.
 *
 * @author Ry, Morgan Gardner
 */
public class SingleTileView implements Initializable {

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = SingleTileView.class.getResource("SingleTileView.fxml");

    /**
     * Event route String ID for tile view, used for redirecting events
     * around an Editor.
     */
    public static final String EVENT_ROUTE_ID = "[SINGLE-TILE-VIEW] :: EVENT";

    /**
     * Unique event name suffix.
     */
    public static final String EVENT_SUFFIX = "[TILE-VIEW-DRAG] :: ";

    /**
     * Inner content borderpane which consists of all the actual view tile
     * nodes. The container of this specifically acts as a padding and spacer.
     */
    @FXML
    private BorderPane innerContentBorderPane;

    /**
     * Root of this view.
     */
    private Parent root;

    /**
     * Factory object that can create new tiles of the target class.
     */
    private Tile.TileFactory<SpriteResource> factory;

    /**
     * The literal tile class this view represents.
     */
    private Class<? extends Tile> tileClass;

    /**
     * The possible display sprites of the target Tile.
     */
    private SpriteResource[] sprites;

    /**
     * Single image display view.
     */
    @FXML
    private ImageView displaySpriteImageResource;


    /**
     * Static constructor which initialises the Tile view for the provided args.
     *
     * @param f          Factory object to create new instances of the target
     *                   tile.
     * @param targetTile The target tile class.
     * @param sprites    The possible display sprites for the target tile.
     * @return Newly loaded and initialised Tile View.
     */
    public static SingleTileView init(final Tile.TileFactory<SpriteResource> f,
                                      final Class<? extends Tile> targetTile,
                                      final SpriteResource... sprites) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            Parent root = loader.load();
            final SingleTileView view = loader.getController();

            view.root = root;
            view.factory = f;
            view.tileClass = targetTile;
            view.sprites = sprites;

            return view;

            // Rethrow as unchecked
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Initialise the view.
     *
     * @param url    Un-used.
     * @param bundle Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {
        Platform.runLater(() -> {
            this.displaySpriteImageResource.setImage(new Image(
                    sprites[0].getResource().toExternalForm()
            ));
        });
    }

    /**
     * Initialises a drag operation providing two essential pieces of
     * information that allow the event to cycle back to itself.
     * <p>
     * Note:
     * <ol>
     *     <li>
     *     {@link SingleTileView#EVENT_ROUTE_ID} is the event routing ID this
     *     is used to identify events produced by this class.
     *     </li>
     *     <li>
     *         {@link CustomEventDataMap#CONTENT} + this.toString() is the
     *         Unique Object identifier used to decipher which view instance
     *         it is.
     *     </li>
     * </ol>
     * <p>
     * This assumes that the target {@link gui.editor.LevelEditor} has had
     * the appropriate event handles set.
     *
     * @param mouseEvent The mouse event to start a drag operation on.
     */
    @FXML
    private void onDragDetected(final MouseEvent mouseEvent) {
        final Dragboard db = this.displaySpriteImageResource.startDragAndDrop(
                TransferMode.ANY
        );
        db.setDragView(displaySpriteImageResource.getImage());

        final CustomEventDataMap cb = new CustomEventDataMap(
                EVENT_ROUTE_ID,
                this.toString()
        );
        db.setContent(cb);

        mouseEvent.consume();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Standard data collection methods/get methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return Tile class type that this view wraps.
     */
    public Class<? extends Tile> getTileClass() {
        return tileClass;
    }

    /**
     * @return All possible display sprites for the target tile.
     */
    public SpriteResource[] getSprites() {
        return sprites;
    }

    /**
     * Creates the target tile using the provided parameters.
     *
     * @param row   The row position of the tile.
     * @param col   The column position of the tile.
     * @param image The display sprite for the tile.
     * @return Newly constructed tile.
     */
    public Tile createTile(final int row,
                           final int col,
                           final SpriteResource image) {
        return factory.create(image, row, col);
    }

    /**
     * @return Root node for this object hierarchy.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return Identifier string of this instance.
     */
    @Override
    public String toString() {
        return EVENT_SUFFIX
                + this.tileClass.getSimpleName();
    }

    /**
     * Removes all borders from the content.
     */
    public void removeSubtleBorder() {
        this.innerContentBorderPane.setStyle(
                "-fx-border-color: transparent"
        );
    }

    /**
     * Adds a rounded lime border to the base content.
     */
    public void addSubtleBorder() {
        this.innerContentBorderPane.setStyle(
                "-fx-border-color: lime; -fx-border-radius: 3px"
        );
    }
}
