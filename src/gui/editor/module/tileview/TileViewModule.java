package gui.editor.module.tileview;

import game.level.reader.module.GameProperties;
import game.tile.Tile;
import gui.editor.LevelEditor;
import gui.editor.module.dependant.LevelEditorModule;
import gui.game.dependant.tilemap.GameMap;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Java class created on 12/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class TileViewModule implements LevelEditorModule {

    private GameMap map;

    private int numRows;
    private int numCols;
    private Tile[][] tileMapRaw;

    /**
     * Loads the module into the level editor scene. So that it can be
     * interacted with.
     * <p>
     * Note that you should store the provided {@link LevelEditor} locally in
     * your class then use that when interacting with something in the editor.
     *
     * @param editor The editor to load into.
     */
    @Override
    public void loadIntoScene(final LevelEditor editor) {
        final GameProperties p
                = editor.getFileToEdit().getDefaultProperties();
        this.numRows = p.getRows();
        this.numCols = p.getColumns();
        this.tileMapRaw = editor.getFileToEdit().getLevel().getTiles();

        // Create the game map
        this.map = new GameMap(
                numRows,
                numCols,
                new GridPaneFactory.CenteredGridPane()
        );

        // Load the tiles into the game map
        for (int row = 0; row < numRows; ++row) {
            for (int col = 0; col < numCols; ++col) {

                final ImageView fxView = tileMapRaw[row][col].getFXSpriteView();
                setInteractiveElement(fxView);

                this.map.setNodeAt(
                        row,
                        col,
                        fxView
                );
            }
        }

        editor.getEditorTileViewBorderpane().setCenter(map.getRoot());
    }

    private void setInteractiveElement(final ImageView view) {

        final FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setFromValue(1.0);
        transition.setToValue(0.5);
        transition.setCycleCount(Transition.INDEFINITE);
        transition.setAutoReverse(true);
        transition.setNode(view);

        view.setOnDragEntered((e) -> {
            transition.playFromStart();
        });

        view.setOnDragExited((e) -> {
            transition.stop();
            view.setOpacity(1.0);
        });
    }
}
