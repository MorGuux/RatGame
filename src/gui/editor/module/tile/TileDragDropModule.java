package gui.editor.module.tile;

import game.tile.SpriteResource;
import game.tile.Tile;
import game.tile.base.tunnel.TunnelSprite;
import game.tile.loader.TileRegex;
import gui.editor.LevelEditor;
import gui.editor.module.dependant.CustomEventDataMap;
import gui.editor.module.dependant.LevelEditorModule;
import gui.editor.module.tile.single.SingleTileView;
import gui.editor.module.tileview.TileViewModule;
import javafx.scene.input.DragEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2. Tile
 * Drag drop module which consists mainly of the Tile drag drop operations
 * and how it interacts with a target {@link LevelEditor}.
 *
 * @author -Ry, Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */
public class TileDragDropModule implements LevelEditorModule {

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
     * Constructs the Tile drag drop module.
     */
    public TileDragDropModule() {
        for (TileRegex r : TileRegex.values()) {
            final SingleTileView view = SingleTileView.init(
                    r.getTileFactory(),
                    r.getTargetClass(),
                    r.getAvailableSprites()
            );

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

        // todo can be lambda: (str, view) -> { foo }
        this.tileViews.forEach(new BiConsumer<String, SingleTileView>() {
            @Override
            public void accept(String s, SingleTileView view) {
                editor.getTilesHBox().getChildren().add(view.getRoot());
            }
        });

        // Register this controller as a drag result handler for the Tiles
        editor.addEventHandle(
                SingleTileView.EVENT_ROUTE_ID,
                this::dragResultHandle
        );
    }

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

        final String s = SingleTileView.EVENT_SUFFIX;
        final String name = (String) event.getDragboard().getContent(
                CustomEventDataMap.CONTENT
        );

        final SingleTileView view = this.tileViews.get(name);


        System.out.println(
                s
                + " :: "
                + view.createTile(row, col, view.getSprites()[0]).buildToString()
        );

        TileViewModule tileView = editor.getTileViewModule();

        // todo sprite enumeration

        tileView.setTile(getTileAt(tileView, view, row, col));
    }

    private Tile getTileAt(TileViewModule tileView, SingleTileView newView,
                           int row, int col) {
        Tile newTile = newView.createTile(row, col, newView.getSprites()[0]);

        Tile[] neighbourTiles = tileView.getAdjacentTiles(row, col);

        SpriteResource spriteType = getSpriteType(neighbourTiles, newTile);

        //todo enumerate adjacent tile sprites to connect tiles

        return newView.createTile(row, col, spriteType);
    }

    private SpriteResource getSpriteType(Tile[] adjacentTiles,
                                         Tile currentTile) {

        //todo change from class names to something else
        //todo change from just tunnel to support all types of tiles

        String currentTileType = currentTile.getClass().getSimpleName();

        System.out.println(currentTile.getClass().getSimpleName());

        String[] adjacentTileTypes = new String[adjacentTiles.length];

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
        } else {
            System.out.println("Tunnel placed alone, no bare tunnel sprite to" +
                    " use.");
            return TunnelSprite.CROSS_ROAD;
        }
    }

    private boolean isCrossroads(String[] adjacentTiles, String currentTile) {
        //crossroads are where all surrounding tiles are the same type
        return Arrays.stream(adjacentTiles).allMatch(t -> t.equals(currentTile));
    }

    private boolean isHorizontal(String[] adjacentTiles, String currentTile) {
        //horizontal is where the two tiles to the left and right are the same
        boolean left = adjacentTiles[3].equals(currentTile);
        boolean right = adjacentTiles[1].equals(currentTile);
        boolean match = adjacentTiles[1].equals(adjacentTiles[3]);
        if (match) {
            if (left && right) {
                return true;
            }
        }
        if (left || right) {
            return !adjacentTiles[0].equals(currentTile) &&
                    !adjacentTiles[2].equals(currentTile);
        }
        return false;
    }

    private boolean isVertical(String[] adjacentTiles, String currentTile) {
        //vertical is where the two tiles above and below are the same
        boolean up = adjacentTiles[0].equals(currentTile);
        boolean down = adjacentTiles[2].equals(currentTile);
        boolean match = adjacentTiles[0].equals(adjacentTiles[2]);
        if (match) {
            if (up && down) {
                return true;
            }
        }
        if (up || down) {
            return !adjacentTiles[1].equals(currentTile) &&
                    !adjacentTiles[3].equals(currentTile);
        }
        return false;
    }

    private TunnelSprite isCorner(String[] adjacentTiles, String currentTile) {
        if (adjacentTiles[0].equals(currentTile) && adjacentTiles[1].equals(currentTile)) {
            return TunnelSprite.TURN_B_RIGHT;
        } else if (adjacentTiles[1].equals(currentTile) && adjacentTiles[2].equals(currentTile)) {
            return TunnelSprite.TURN_F_RIGHT;
        } else if (adjacentTiles[2].equals(currentTile) && adjacentTiles[3].equals(currentTile)) {
            return TunnelSprite.TURN_F_LEFT;
        } else if (adjacentTiles[3].equals(currentTile) && adjacentTiles[0].equals(currentTile)) {
            return TunnelSprite.TURN_B_LEFT;
        } else {
            return null;
        }
    }
}
