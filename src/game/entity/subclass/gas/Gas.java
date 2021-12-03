package game.entity.subclass.gas;

import game.RatGame;
import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.contextmap.TileDataNode;
import game.entity.Item;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.Tile;
import game.tile.base.path.Path;
import game.tile.base.tunnel.Tunnel;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Gas.java - A gas item.
 * Uses the Entity class as a base.
 * Once placed, it will start spreading at specific speed, killing all
 * rats that have been within the impact radius for a given amount of time.
 * It can then be removed from the game.
 *
 * @author Ashraf Said
 * @version 0.1
 * Copyright: N/A
 */

public class Gas extends Item {

    /**
     * Gas explode image resource.
     */
    private static final URL GAS_IMAGE
            = Gas.class.getResource("assets/Gas.png");

    /**
     * Duration of Gas in ticks
     */
    private static final int DURATION = 11;

    /**
     * Current amount of ticks gas has been present on the map.
     */
    private int currentTickTime;

    /**
     * List storing tiles lately occupied.
     */
    private List<TileData> tilesLatelyOccupied;

    /**
     * Builds a Bomb object from the provided args string.
     *
     * @param args Arguments used to build a bomb.
     * @return Newly constructed Bomb.
     */
    public static Gas build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = 3;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[0]);
            final int col = Integer.parseInt(args[1]);
            final int health = Integer.parseInt(args[2]);

            return new Gas(row, col, health);
        } catch (Exception e) {
            throw new InvalidArgsContent(Arrays.deepToString(args));
        }
    }

    /**
     * Construct an Entity from the base starting Row and Column.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     */
    public Gas(final int initRow,
               final int initCol) {
        super(initRow, initCol);
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public Gas(final int initialRow,
               final int initialCol,
               final int curHealth) {
        super(initialRow, initialCol, curHealth);
    }

    /**
     * Place where this Gas item can be updated and, do something once
     * provided some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this Gas item.
     * @implNote Both Objects are Object because we don't have
     * implementations for these objects just yet.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        //TODO : Implement gas update. Will request all rats within
        // a radius of this item and will kill them after a given set of time
        // (gradually lowers health) after a set duration.
        if (currentTickTime == 0) {
            this.initializeTileQueue(contextMap);
        }
        if (currentTickTime < 4) {
            this.spread(contextMap);
        } else if (currentTickTime < 7) {
            this.remain(contextMap);
        } else if (currentTickTime < 11) {
            this.deOccupy(contextMap);
        } else {
            System.out.println("GAS SHOULD BE REMOVED");
        }

        this.currentTickTime++;
    }

    /**
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    @Override
    public URL getDisplaySprite() {
        return GAS_IMAGE;
    }

    /**
     * Build the Gas item to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Gas class.
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        final TileData[] occupied = contextMap.getTilesOccupied(this);

        return String.format("[Gas, [%s,%s,%s], [%s]]",
                getRow(),
                getCol(),
                getHealth(),
                formatPositions(occupied, this)
        );
    }

    /**
     * Spreads the gas further, occupying next entities.
     * @param contextMap map containing information about tiles in the game.
     */
    private void spread(final ContextualMap contextMap) {
        System.out.println("GAS SPREADING");

        //directions to check
        CardinalDirection[] directions = {
                CardinalDirection.NORTH,
                CardinalDirection.EAST,
                CardinalDirection.SOUTH,
                CardinalDirection.WEST
        };


        ArrayList<TileData> tilesLatelyOccupiedCopy =
                new ArrayList<>(tilesLatelyOccupied);
        for (TileData tileData : tilesLatelyOccupiedCopy) {
            System.out.println("TileData(" + tileData.getRow() + "," + tileData.getCol() + ") :");
            for (CardinalDirection dir : directions) {
                TileData destinationTile = this.getDestinationTile(contextMap,
                        tileData, dir);

                //check if tile is transferable (not occupied yet)
                if ((destinationTile.getTile() instanceof Path
                        || destinationTile.getTile() instanceof Tunnel)
                        && !Arrays.asList(contextMap.getTilesOccupied(this)).contains(destinationTile)) {

                    contextMap.occupyCoordinate(this,
                            dir.traverse(tileData.getRow(), tileData.getCol()));

                    tilesLatelyOccupied.add(destinationTile);
                    tilesLatelyOccupied.remove(tileData);
                    System.out.println("Tile " + dir + " added.");
                }
            }
            System.out.println("====================");
        }
    }

    /**
     * todo comment on this one please
     * @param contextMap map containing information about tiles in the game.
     */
    private void remain(final ContextualMap contextMap) {
        System.out.println("GAS REMAINING");
    }

    /**
     * Vanish gas slowly, de-occupying entities affected.
     * @param contextMap map containing information about tiles in the game.
     */
    private void deOccupy(final ContextualMap contextMap) {
        System.out.println("GAS DEOCCUPYING");
    }

    /**
     * Initializes lately occupied tiles with initial tile.
     * @param contextMap
     */
    private void initializeTileQueue(final ContextualMap contextMap) {
        tilesLatelyOccupied = new ArrayList<>();
        tilesLatelyOccupied.add(contextMap.getTileDataAt(this.getRow(),
                this.getCol()));
    }

    /**
     * Gets a destination tile with given initial tile and direction.
     * @param contextMap The map that this entity may exist on.
     * @param tileData Origin tile.
     * @param dir Direction to go.
     * @return
     */
    private TileData getDestinationTile(final ContextualMap contextMap,
                                        final TileData tileData,
                                        final CardinalDirection dir) {
        TileData destinationTile;

        if (dir == CardinalDirection.NORTH) {
            destinationTile = contextMap.getTileDataAt(tileData.getRow() - 1,
                    tileData.getCol());
        } else if (dir == CardinalDirection.EAST) {
            destinationTile = contextMap.getTileDataAt(tileData.getRow(),
                    tileData.getCol() + 1);
        } else if (dir == CardinalDirection.SOUTH) {
            destinationTile = contextMap.getTileDataAt(tileData.getRow() + 1,
                    tileData.getCol());
        } else {
            destinationTile = contextMap.getTileDataAt(tileData.getRow(),
                    tileData.getCol() - 1);
        }

        return destinationTile;
    }
}
