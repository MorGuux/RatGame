package gui.editor.module.tile;

import game.tile.SpriteResource;
import game.tile.Tile;
import game.tile.base.tunnel.Tunnel;
import game.tile.base.tunnel.TunnelSprite;
import game.tile.loader.TileRegex;
import gui.editor.LevelEditor;
import gui.editor.module.dependant.CustomEventDataMap;
import gui.editor.module.dependant.LevelEditorModule;
import gui.editor.module.dependant.LevelEditorMouseHandler;
import gui.editor.module.grid.tileview.TileViewModule;
import gui.editor.module.tile.single.SingleTileView;
import javafx.event.EventType;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import util.SceneUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2. Tile
 * Drag drop module which consists mainly of the Tile drag drop operations
 * and how it interacts with a target {@link LevelEditor}.
 *
 * @author -Ry, Morgan Gardner
 * @version 0.2
 * Copyright: N/A
 */
public class TileDragDropModule
        implements LevelEditorModule, LevelEditorMouseHandler {

    /**
     * A map consisting of unique Object identifier strings and the actual
     * object instance.
     * <p>
     * I.e., String = instance.toString(); map.get(String) = instance;
     */
    private final Map<String, SingleTileView> tileViews = new HashMap<>();

    /**
     * The level editor that this module interacts with.
     */
    private LevelEditor editor;

    /**
     * Should mouse events trigger grid action events, causing the mouse to
     * draw tiles.
     */
    private final AtomicBoolean isDrawModeEnabled = new AtomicBoolean(false);

    /**
     * The current view used to draw tiles.
     */
    private final AtomicReference<SingleTileView> selectedView
            = new AtomicReference<>();

    /**
     * Constructs the Tile drag drop module.
     */
    public TileDragDropModule() {
        for (TileRegex r : TileRegex.values()) {
            final SingleTileView view = SingleTileView.init(
                    r.getTileFactory(),
                    r.getTargetClass(),
                    r.getAvailableSprites()
            );

            view.getRoot().setOnMouseClicked((e) -> {
                if (SceneUtil.wasLeftClick(e)) {
                    this.setSelectedView(view);
                }
            });
            tileViews.put(view.toString(), view);
        }
    }

    /**
     * Loads the module into the level editor scene. So that it can be
     * interacted with. alongside registering even handles as well.
     *
     * @param editor The editor to load into.
     */
    @Override
    public void loadIntoScene(final LevelEditor editor) {
        this.editor = editor;

        this.editor.addMouseEventHandle(this);
        this.tileViews.forEach((s, view) -> {
            editor.getTilesHBox().getChildren().add(view.getRoot());
        });

        // Toggle button for cursor drawing (default state is disabled)
        final ToggleButton toggleButton = new ToggleButton();
        toggleButton.setText("Draw Mode");
        toggleButton.setSelected(false);
        toggleButton.selectedProperty().addListener((p, o, n) -> {
            this.setIsDrawModeEnabled(n);
        });
        SceneUtil.applyStyle(toggleButton);
        toggleButton.setStyle("-fx-border-color: red; -fx-border-radius: 3px");
        toggleButton.setOnAction((e) -> {
            final double defaultValue = 1.0;
            final double clickedValue = 0.9;

            // Button active
            if (toggleButton.isSelected()) {
                toggleButton.setScaleX(clickedValue);
                toggleButton.setScaleY(clickedValue);
                toggleButton.setStyle(
                        "-fx-border-color: lime; -fx-border-radius: 3px"
                );

                // Button inactive
            } else {
                toggleButton.setScaleX(defaultValue);
                toggleButton.setScaleY(defaultValue);
                toggleButton.setStyle(
                        "-fx-border-color: red; -fx-border-radius: 3px"
                );
            }
        });
        editor.getTilesHBox().getChildren().add(toggleButton);

        // Register this controller as a drag result handler for the Tiles
        editor.addEventHandle(
                SingleTileView.EVENT_ROUTE_ID,
                this::dragResultHandle
        );
    }

    /**
     * Handles any and all mouse event types that can occur, such as Mouse
     * Clicked or Mouse Dragged.
     *
     * @param type  The actual event type such as Drag, or Click.
     * @param event The event data.
     * @param row   The corresponding Row position in the display grid.
     * @param col   The corresponding Col position in the display grid.
     */
    @Override
    public void handle(final EventType<MouseEvent> type,
                       final MouseEvent event,
                       final int row,
                       final int col) {

        // Mouse drawing only if enabled
        if (isDrawModeEnabled()) {

            // Disable the pannable state of the scroll pane (stop the mouse
            // action from moving the scrollbars)
            if (type.equals(MouseEvent.MOUSE_PRESSED)
                    && SceneUtil.wasLeftClick(event)) {
                this.editor.setPannable(false);


                // Draw tiles on drag
            } else if ((type.equals(MouseEvent.MOUSE_DRAGGED)
                    || type.equals(MouseEvent.MOUSE_CLICKED))
                    && SceneUtil.wasLeftClick(event)) {

                final SingleTileView v = this.selectedView.get();
                if (v != null) {
                    this.setTile(v, this.editor.getTileViewModule(), row, col);
                }

                // Once drag mouse released re-enable the drag capability
            } else if (type.equals(MouseEvent.MOUSE_RELEASED)
                    && SceneUtil.wasLeftClick(event)) {
                this.editor.setPannable(true);
            }
        }
    }

    /**
     * @return {@code true} if mouse events should be handled to draw tiles.
     * Else if not then {@code false} is returned.
     */
    public boolean isDrawModeEnabled() {
        return this.isDrawModeEnabled.get();
    }

    /**
     * Sets the state of the draw mode to the provided state.
     *
     * @param state The draw mode state where true indicates enabled.
     */
    public void setIsDrawModeEnabled(final boolean state) {
        this.isDrawModeEnabled.getAndSet(state);
    }

    /**
     * Sets the view used when drawing tiles to the provided view.
     *
     * @param view The view to use.
     */
    private void setSelectedView(final SingleTileView view) {
        final SingleTileView cur = this.selectedView.getAndSet(view);

        if (cur != null) {
            SceneUtil.fadeInNode(cur.getRoot());
            cur.removeSubtleBorder();
        }

        SceneUtil.scaleNodeIn(view.getRoot());
        view.addSubtleBorder();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Drag and drop for tiles
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Handles a drag event that occurred in a level editor.
     *
     * @param editor The editor that detected the drag event.
     * @param event  The event that occurred.
     * @param row    The row position in the grid map of this events final
     *               mouse position.
     * @param col    The col position in the grid map of this events final mouse
     *               position.
     */
    private void dragResultHandle(final LevelEditor editor,
                                  final DragEvent event,
                                  final int row,
                                  final int col) {
        // todo clean up if you want; could also remove the event handles if
        //  not needed.

        final String name = (String) event.getDragboard().getContent(
                CustomEventDataMap.CONTENT
        );

        final SingleTileView view = this.tileViews.get(name);
        final TileViewModule tileView = editor.getTileViewModule();

        setTile(view, tileView, row, col);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Tile drag drop intermediate actions and calculations
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sets the tile at the provided row col to the provided view, does so
     * determining specifically what sprite and if any adjacent tiles need to
     * be updated alongside this.
     *
     * @param view     The view which contains the tile to draw.
     * @param tileView The view that we can draw into.
     * @param row      The row position to place the tile at.
     * @param col      The column position to place the tile at.
     */
    private synchronized void setTile(final SingleTileView view,
                                      final TileViewModule tileView,
                                      final int row,
                                      final int col) {

        // If tunnel tile then determine nearby tiles
        if (view.getTileClass().equals(Tunnel.class)) {
            tileView.setTile(getTileAt(tileView, view, row, col));

            //enumerate neighbouring tiles to update sprites
            final Tile[] neighbourTiles = tileView.getAdjacentTiles(row, col);
            for (Tile neighbour : neighbourTiles) {
                if (neighbour != null) {
                    if (neighbour.getClass().equals(Tunnel.class)) {
                        tileView.setTile(getTileAt(
                                tileView,
                                view,
                                neighbour.getRow(),
                                neighbour.getCol()
                        ));
                    }
                }
            }

            // Just set the tile as is
        } else {
            tileView.setTile(view.createTile(row, col, view.getSprites()[0]));
        }
    }

    /**
     * Gets the tile at the provided row and col.
     * @param tileView The level editor's tile view module.
     * @param newView The view that contains the tile to draw.
     * @param row The row position to place the tile at.
     * @param col The column position to place the tile at.
     * @return The tile at the provided row and col.
     */
    private Tile getTileAt(final TileViewModule tileView,
                           final SingleTileView newView,
                           final int row,
                           final int col) {
        final Tile newTile = newView.createTile(
                row,
                col,
                newView.getSprites()[0]
        );

        final Tile[] neighbourTiles = tileView.getAdjacentTiles(row, col);
        final SpriteResource spriteType = getSpriteType(
                neighbourTiles,
                newTile
        );

        return newView.createTile(row, col, spriteType);
    }

    /**
     * Gets the sprite type of the provided tile, given the adjacent tiles.
     * @param adjacentTiles The adjacent tiles to the provided tile.
     * @param currentTile The provided tile.
     * @return The sprite type of the provided tile.
     */
    private SpriteResource getSpriteType(final Tile[] adjacentTiles,
                                         final Tile currentTile) {

        //Convert adjacent tiles to a list of tile types
        final Class<?> currentTileType = currentTile.getClass();
        final Class<?>[] adjacentTileTypes = new Class<?>[adjacentTiles.length];

        for (int i = 0; i < adjacentTiles.length; i++) {
            if (adjacentTiles[i] != null) {
                adjacentTileTypes[i] = adjacentTiles[i]
                        .getClass();
            }
        }

        //Ordered by most connections to least.
        if (isCrossroads(adjacentTileTypes, currentTileType)) {
            return TunnelSprite.CROSS_ROAD;

        } else if (getTJunction(adjacentTileTypes, currentTileType) != null) {
            return getTJunction(adjacentTileTypes, currentTileType);

        } else if (getCorner(adjacentTileTypes, currentTileType) != null) {
            return getCorner(adjacentTileTypes, currentTileType);

        } else if (isVertical(adjacentTileTypes, currentTileType)) {
            return TunnelSprite.VERTICAL;

        } else if (isHorizontal(adjacentTileTypes, currentTileType)) {
            return TunnelSprite.HORIZONTAL;

        }
        // Default case
        else {
            return TunnelSprite.VERTICAL;
        }
    }

    /**
     * Checks if the provided tile is a crossroad.
     * I.E. are all adjacent tiles the same type as the current.
     * @param adjacentTiles The adjacent tiles to the provided tile.
     * @param currentTile The provided tile.
     * @return True if the provided tile is a crossroad, false otherwise.
     */
    private boolean isCrossroads(final Class<?>[] adjacentTiles,
                                 final Class<?> currentTile) {
        //crossroads are where all surrounding tiles are the same type
        return Arrays.stream(adjacentTiles)
                .allMatch(t -> Objects.equals(t, currentTile));
    }

    /**
     * Checks if the provided tile is a horizontal tile.
     * I.E. the tiles either side horizontally match the current tile type.
     * @param adjacentTiles The adjacent tiles to the provided tile.
     * @param currentTile The provided tile.
     * @return True if the provided tile is a horizontal tile, false otherwise.
     */
    private boolean isHorizontal(final Class<?>[] adjacentTiles,
                                 final Class<?> currentTile) {
        //horizontal is where the two tiles to the left and right are the same
        final boolean left
                = Objects.equals(adjacentTiles[3], currentTile);
        final boolean right
                = Objects.equals(adjacentTiles[1], currentTile);
        final boolean match
                = Objects.equals(adjacentTiles[1], adjacentTiles[3]);

        if (match) {
            if (left && right) {
                return true;
            }
        }

        if (left || right) {
            return !Objects.equals(adjacentTiles[0], currentTile)
                    && !Objects.equals(adjacentTiles[2], currentTile);
        }

        //possible edge tile
        if (adjacentTiles[1] == null
                || adjacentTiles[3] == null) {
            return !Objects.equals(adjacentTiles[0], currentTile)
                    && !Objects.equals(adjacentTiles[2], currentTile);
        }

        return false;
    }

    /**
     * Checks if the provided tile is a vertical tile.
     * I.E. the tiles either side vertically match the current tile type.
     * @param adjacentTiles The adjacent tiles to the provided tile.
     * @param currentTile The provided tile.
     * @return True if the provided tile is a vertical tile, false otherwise.
     */
    private boolean isVertical(final Class<?>[] adjacentTiles,
                               final Class<?> currentTile) {
        //vertical is where the two tiles above and below are the same
        final boolean up
                = Objects.equals(adjacentTiles[0], currentTile);
        final boolean down
                = Objects.equals(adjacentTiles[2], currentTile);
        final boolean match
                = Objects.equals(adjacentTiles[0], adjacentTiles[2]);

        if (match) {
            if (up && down) {
                return true;
            }
        }

        if (up || down) {
            return !Objects.equals(adjacentTiles[1], currentTile)
                    && !Objects.equals(adjacentTiles[3], currentTile);
        }

        //possible edge tile
        if (adjacentTiles[0] == null || adjacentTiles[2] == null) {
            return !Objects.equals(adjacentTiles[1], currentTile)
                    && !Objects.equals(adjacentTiles[3], currentTile);
        }

        return false;
    }

    /**
     * Checks if the provided tile is a corner tile.
     * I.E. the tiles on two perpendicular sides match the current tile type.
     * @param adjacentTiles The adjacent tiles to the provided tile.
     * @param currentTile The provided tile.
     * @return The orientation of the corner, or null if the tile is not a
     * corner tile.
     */
    private TunnelSprite getCorner(final Class<?>[] adjacentTiles,
                                   final Class<?> currentTile) {

        if (Objects.equals(adjacentTiles[0], currentTile)
                && Objects.equals(adjacentTiles[1], currentTile)) {
            return TunnelSprite.TURN_B_RIGHT;

        } else if (Objects.equals(adjacentTiles[1], currentTile)
                && Objects.equals(adjacentTiles[2], currentTile)) {
            return TunnelSprite.TURN_F_RIGHT;

        } else if (Objects.equals(adjacentTiles[2], currentTile)
                && Objects.equals(adjacentTiles[3], currentTile)) {
            return TunnelSprite.TURN_F_LEFT;

        } else if (Objects.equals(adjacentTiles[3], currentTile)
                && Objects.equals(adjacentTiles[0], currentTile)) {
            return TunnelSprite.TURN_B_LEFT;

        } else {
            return null;
        }
    }

    /**
     * Checks if the provided tile is a t-junction tile.
     * I.E. the tiles on three sides match the current tile type.
     * @param adjacentTiles The adjacent tiles to the provided tile.
     * @param currentTile The provided tile.
     * @return The orientation of the t-junction, or null if the tile is not a
     * t-junction tile.
     */
    private TunnelSprite getTJunction(final Class<?>[] adjacentTiles,
                                  final Class<?> currentTile) {

        if (Objects.equals(adjacentTiles[0], currentTile)
                && Objects.equals(adjacentTiles[1], currentTile)
                && Objects.equals(adjacentTiles[3], currentTile)) {
            return TunnelSprite.T_JUNCTION_UP;

        } else if (Objects.equals(adjacentTiles[1], currentTile)
                && Objects.equals(adjacentTiles[2], currentTile)
                && Objects.equals(adjacentTiles[3], currentTile)) {
            return TunnelSprite.T_JUNCTION_DOWN;

        } else if (Objects.equals(adjacentTiles[0], currentTile)
                && Objects.equals(adjacentTiles[1], currentTile)
                && Objects.equals(adjacentTiles[2], currentTile)) {
            return TunnelSprite.T_JUNCTION_LEFT;

        } else if (Objects.equals(adjacentTiles[0], currentTile)
                && Objects.equals(adjacentTiles[2], currentTile)
                && Objects.equals(adjacentTiles[3], currentTile)) {
            return TunnelSprite.T_JUNCTION_RIGHT;

        } else {
            return null;
        }
    }
}
