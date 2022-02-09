package game.entity.subclass.gas;

import game.RatGame;
import game.classinfo.tags.TargetConstructor;
import game.classinfo.tags.WritableField;
import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.contextmap.TileDataNode;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.rat.Rat;
import game.event.impl.entity.specific.general.EntityDeOccupyTileEvent;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityOccupyTileEvent;
import game.event.impl.entity.specific.general.GenericAudioEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.base.grass.Grass;
import game.tile.base.grass.GrassSprite;
import game.tile.base.path.Path;
import game.tile.base.tunnel.Tunnel;
import gui.game.EventAudio.GameAudio;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Gas.java - A gas item.
 * Uses the Entity class as a base.
 * Once placed, it will start spreading at specific speed, killing all
 * rats that have been within the impact radius for a given amount of time.
 * It can then be removed from the game.
 *
 * @author Jakub Wozny
 * @version 0.3
 * Copyright: N/A
 */

public class Gas extends Item {

    /**
     * Gas explode image resource.
     */
    private static final URL GAS_IMAGE
            = Gas.class.getResource("assets/Gas.png");

    /**
     * Index of the row in the arguments build array.
     */
    private static final int ROW_INDEX = 0;

    /**
     * Index of the col in the arguments build array.
     */
    private static final int COL_INDEX = 1;

    /**
     * Index of the health in the arguments build array.
     */
    private static final int HEALTH_INDEX = 2;

    /**
     * Index of the current time in the arguments build array.
     */
    private static final int CURRENT_TIME_INDEX = 3;

    /**
     * Index of the current time in the arguments build array.
     */
    private static final int TILES_QUEUE_INDEX = 4;

    /**
     * Expected arguments number for arguments array in build.
     */
    private static final int EXPECTED_ARGUMENTS_NUMBER = 5;

    /**
     * Damage given to the rat occupying gas tile.
     */
    private static final int DAMAGE_GIVEN = 20;

    /**
     * Duration of Gas in ticks.
     */
    private static final int DURATION = 11;

    /**
     * Number of ticks which it takes for Gas to do something.
     */
    private static final int TICK_DIFFERENCE = 4;

    /**
     * n * TICK_DIFFERENCE where n is the number of spreads - 1.
     */
    private static final int SPREAD_TIME = 3 * TICK_DIFFERENCE + 1;

    /**
     * n * TICK_DIFFERENCE where n is the number of waits between spread and
     * de-occupy.
     */
    private static final int WAIT_TIME = 3 * TICK_DIFFERENCE + SPREAD_TIME;

    /**
     * n * TICK_DIFFERENCE where n is the number of de-occupy.
     */
    private static final int DE_OCCUPY_TIME = 4 * TICK_DIFFERENCE + WAIT_TIME;

    /**
     * Current amount of ticks gas has been present on the map.
     */
    @WritableField(name = "Gas Existence Time",
            defaultValue = "0")
    private int currentTickTime;

    /**
     * Un-parsed tilesLatelyOccupied. Stored in the format "x:y;x:y". Used in
     * build method, have to be parsed latter as we don't have access to
     * ContextualMap which is essential.
     */
    private String unparsedTilesLatelyUpdated;

    /**
     * List storing tiles lately occupied.
     */
    private List<TileData> tilesLatelyOccupied;

    /**
     * Builds a Gas object from the provided args string.
     *
     * @param args Arguments used to build a gas.
     * @return Newly constructed Gas.
     * @throws ImproperlyFormattedArgs if the String can not be parsed.
     * @throws InvalidArgsContent      if the arguments are not formatted correctly.
     */
    public static Gas build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = EXPECTED_ARGUMENTS_NUMBER;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[ROW_INDEX]);
            final int col = Integer.parseInt(args[COL_INDEX]);
            final int health = Integer.parseInt(args[HEALTH_INDEX]);
            final int currentTick = Integer.parseInt(args[CURRENT_TIME_INDEX]);
            final String unparsedTilesLatelyUpdated = args[TILES_QUEUE_INDEX];

            return new Gas(row, col, health, currentTick,
                    unparsedTilesLatelyUpdated);
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
    @TargetConstructor
    public Gas(final int initRow,
               final int initCol) {
        super(initRow, initCol);
        // This is a hacky approach because I have no idea how Gas is
        // implemented lol
        this.unparsedTilesLatelyUpdated = String.format(
                "%s:%s;",
                initRow,
                initCol
        );
        this.tilesLatelyOccupied = new ArrayList<>();
        final Grass g = new Grass(GrassSprite.BARE_GRASS, initRow, initCol);
        tilesLatelyOccupied.add(new TileData(new TileDataNode(g)));
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
     * Construct an Entity from the base starting x, y, health value and
     * current tick time.
     *
     * @param initialRow         Row in a 2D Array. A[ROW][COL]
     * @param initialCol         Col in a 2D Array. A[ROW][COL]
     * @param curHealth          Current health of the Entity.
     * @param currentGasTickTime Current tick time.
     */
    public Gas(final int initialRow,
               final int initialCol,
               final int curHealth,
               final int currentGasTickTime) {
        super(initialRow, initialCol, curHealth);
        this.currentTickTime = currentGasTickTime;
    }

    /**
     * Construct an Entity from the base starting x, y, health value,
     * current tick time and un-parsed tilesLatelyUpdated.
     *
     * @param initialRow         Row in a 2D Array. A[ROW][COL]
     * @param initialCol         Col in a 2D Array. A[ROW][COL]
     * @param curHealth          Current health of the Entity.
     * @param currentGasTickTime Current tick time.
     * @param tilesLatelyUpdated Unparsed queue of tiles.
     */
    public Gas(final int initialRow,
               final int initialCol,
               final int curHealth,
               final int currentGasTickTime,
               final String tilesLatelyUpdated) {
        super(initialRow, initialCol, curHealth);
        this.currentTickTime = currentGasTickTime;
        this.unparsedTilesLatelyUpdated = tilesLatelyUpdated;
    }

    /**
     * Spreads, damages the rates and de-occupies dependent on current tick.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this Gas item.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        if (tilesLatelyOccupied == null) {
            if (unparsedTilesLatelyUpdated == null) {
                this.initializeTileQueue(contextMap);
            } else {
                loadTilesLatelyOccupied(contextMap);
                unparsedTilesLatelyUpdated = null;
            }
        }

        //handle spreading/ de-occupying
        if (currentTickTime % TICK_DIFFERENCE == 0) {
            if (currentTickTime < SPREAD_TIME) {
                this.spread(contextMap);
            } else if (currentTickTime < DE_OCCUPY_TIME) {
                this.deOccupy(contextMap);
            } else {
                this.kill();
                this.fireEvent(new EntityDeOccupyTileEvent(this,
                        this.getRow(),
                        this.getCol()));
            }
        }
        damageRats(contextMap);

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
     *                   [Gas, [0, 0, 100, 20, x:y;x:y;x:y;x:y;x:y], [occupied]]
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        final TileData[] occupied = contextMap.getTilesOccupied(this);

        return String.format("[Gas, [%s,%s,%s,%s,%s], [%s]]",
                getRow(),
                getCol(),
                getHealth(),
                getCurrentTickTime(),
                formatTilesLatelyOccupied(),
                formatPositions(occupied, this)
        );
    }

    /**
     * Spreads the gas further, occupying next entities.
     *
     * @param contextMap map containing information about tiles in the game.
     */
    private void spread(final ContextualMap contextMap) {
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
            for (CardinalDirection dir : directions) {
                TileData destinationTile = this.getDestinationTile(contextMap,
                        tileData, dir);

                //check if tile is transferable (not occupied yet)
                if ((destinationTile.getTile() instanceof Path
                        || destinationTile.getTile() instanceof Tunnel)
                        && !Arrays.asList(contextMap.getTilesOccupied(this))
                        .contains(destinationTile)) {

                    contextMap.occupyCoordinate(this,
                            dir.traverse(tileData.getRow(), tileData.getCol()));

                    if (destinationTile.getTile() instanceof Path) {
                        //display new gas
                        this.fireEvent(new EntityOccupyTileEvent(
                                this,
                                destinationTile.getRow(),
                                destinationTile.getCol(),
                                0,
                                GAS_IMAGE,
                                null));
                    }

                    // Play a nice fart sound
                    this.fireEvent(new GenericAudioEvent(
                            this,
                            GameAudio.GAS.getResource()
                    ));

                    tilesLatelyOccupied.add(destinationTile);
                    tilesLatelyOccupied.remove(tileData);
                }
            }
        }
    }

    /**
     * Returns current time in ticks.
     *
     * @return current time in ticks.
     */
    public int getCurrentTickTime() {
        return currentTickTime;
    }

    /**
     * Vanish gas slowly, de-occupying entities affected.
     *
     * @param contextMap map containing information about tiles in the game.
     */
    private void deOccupy(final ContextualMap contextMap) {
        //directions to check
        final CardinalDirection[] directions = {
                CardinalDirection.NORTH,
                CardinalDirection.EAST,
                CardinalDirection.SOUTH,
                CardinalDirection.WEST
        };

        final ArrayList<TileData> tilesLatelyOccupiedCopy
                = new ArrayList<>(tilesLatelyOccupied);
        for (TileData tileData : tilesLatelyOccupiedCopy) {
            //deOccupy the tile
            this.fireEvent(new EntityDeOccupyTileEvent(
                    this,
                    tileData.getRow(),
                    tileData.getCol()
            ));

            contextMap.deOccupyTile(this, tileData);

            //remove from the list
            tilesLatelyOccupied.remove(tileData);

            for (CardinalDirection dir : directions) {
                TileData destinationTile = this.getDestinationTile(contextMap,
                        tileData, dir);

                //check if tile is occupied by the gas
                if (Arrays.asList(contextMap.getTilesOccupied(this))
                        .contains(destinationTile)
                        && !tilesLatelyOccupied.contains(tileData)) {
                    //add adjacent tileDatas to the queue
                    tilesLatelyOccupied.add(destinationTile);
                }
            }
        }
    }

    /**
     * Initializes lately occupied tiles with initial tile.
     *
     * @param contextMap map containing information about tiles in the game.
     */
    private void initializeTileQueue(final ContextualMap contextMap) {
        tilesLatelyOccupied = new ArrayList<>();
        tilesLatelyOccupied.add(contextMap.getTileDataAt(this.getRow(),
                this.getCol()));
    }

    /**
     * Damages rat that is located on any gas tile.
     *
     * @param contextMap map containing information about tiles in the game.
     */
    private void damageRats(final ContextualMap contextMap) {
        for (TileData tileData : contextMap.getTilesOccupied(this)) {
            for (Entity entity : tileData.getEntities()) {
                if (entity instanceof Rat) {
                    ((Rat) entity).damage(DAMAGE_GIVEN);
                }
            }
        }
    }

    /**
     * Gets a destination tile with given initial tile and direction.
     *
     * @param contextMap The map that this entity may exist on.
     * @param tileData   Origin tile.
     * @param dir        Direction to go.
     * @return tile from the origin tile in given direction.
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

    /**
     * Formats tilesLatelyOccupied this way: x:y;x:y
     * where:   x is row,
     * y is col,
     * ; implies next tile.
     *
     * @return formatted tilesLatelyOccupied as String.
     */
    private String formatTilesLatelyOccupied() {
        String result = "";

        for (TileData tileData : tilesLatelyOccupied) {
            result += String.format("%s:%s;", tileData.getRow(),
                    tileData.getCol());
        }

        //remove last ';'
        if (tilesLatelyOccupied.size() > 0) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    /**
     * Loads the tiles queue from the String given
     *
     * @param contextMap The map that this entity may exist on.
     */
    private void loadTilesLatelyOccupied(final ContextualMap contextMap) {
        String[] pairs = unparsedTilesLatelyUpdated.split(";");

        ArrayList<TileData> result = new ArrayList<>();
        for (String pair : pairs) {
            int row = Integer.parseInt(pair.split(":")[0]);
            int col = Integer.parseInt(pair.split(":")[1]);

            result.add(contextMap.getTileDataAt(row, col));
        }

        this.tilesLatelyOccupied = result;
    }

    /**
     * Kills the gas.
     */
    @Override
    public void kill() {
        super.kill();

        this.fireEvent(new EntityDeathEvent(this, null, null));

    }

    /**
     * Called by the loader object of the Entity for when it is placing the
     * entity into the game map. Calls to this method can be 0 to many
     * however should not ever contain null values for the data unless the
     * args itself were improper.
     *
     * @param occupied The tile that the builder assigned this entity to; The
     *                 tile that was occupied.
     * @param map      The contextual map that this entity is being built/placed
     *                 into.
     */
    @Override
    public void positionOccupiedByLoader(final TileData occupied,
                                         final ContextualMap map) {
        // If not a tunnel then display gas
        if (!(occupied.getTile() instanceof Tunnel)) {
            super.positionOccupiedByLoader(occupied, map);
        }

        // If it is grass then this is malformed, kill the gas.
        if (occupied.getTile() instanceof Grass) {
            this.kill();
        }
    }
}
