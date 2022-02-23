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
 * @version 0.1
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

    private final AtomicBoolean isDrawModeEnabled = new AtomicBoolean(false);

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

    public boolean isDrawModeEnabled() {
        return this.isDrawModeEnabled.get();
    }

    public void setIsDrawModeEnabled(final boolean state) {
        this.isDrawModeEnabled.getAndSet(state);
    }

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

    private SpriteResource getSpriteType(final Tile[] adjacentTiles,
                                         final Tile currentTile) {

        //todo change from class names to something else
        //todo use constants with proper names or replace the Tile[] with an
        // object that allows North, East, South, West access.
        //todo change from just tunnel to support all types of tiles

        final String currentTileType = currentTile.getClass().getSimpleName();

        System.out.println(currentTile.getClass().getSimpleName());

        final String[] adjacentTileTypes = new String[adjacentTiles.length];

        for (int i = 0; i < adjacentTiles.length; i++) {
            if (adjacentTiles[i] != null) {
                adjacentTileTypes[i] = adjacentTiles[i]
                        .getClass()
                        .getSimpleName();
            }
        }

        if (isCrossroads(adjacentTileTypes, currentTileType)) {
            return TunnelSprite.CROSS_ROAD;

        } else if (isVertical(adjacentTileTypes, currentTileType)) {
            return TunnelSprite.VERTICAL;

        } else if (isHorizontal(adjacentTileTypes, currentTileType)) {
            return TunnelSprite.HORIZONTAL;

        } else if (isCorner(adjacentTileTypes, currentTileType) != null) {
            return isCorner(adjacentTileTypes, currentTileType);

            // Default case
        } else {
            return TunnelSprite.VERTICAL;
        }
    }

    private boolean isCrossroads(final String[] adjacentTiles,
                                 final String currentTile) {
        //crossroads are where all surrounding tiles are the same type
        return Arrays.stream(adjacentTiles)
                .allMatch(t -> Objects.equals(t, currentTile));
    }

    private boolean isHorizontal(final String[] adjacentTiles,
                                 final String currentTile) {
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

    private boolean isVertical(final String[] adjacentTiles,
                               final String currentTile) {
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

    private TunnelSprite isCorner(final String[] adjacentTiles,
                                  final String currentTile) {

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
}
