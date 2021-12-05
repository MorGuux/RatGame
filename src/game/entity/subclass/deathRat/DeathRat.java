package game.entity.subclass.deathRat;

import game.RatGame;
import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.contextmap.handler.MovementHandler;
import game.contextmap.handler.result.MovementResult;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.noentry.NoEntry;
import game.entity.subclass.rat.Rat;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityMovedEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.Tile;
import game.tile.base.grass.Grass;
import game.tile.base.tunnel.Tunnel;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Rat.java - A death rat entity.
 * Uses the Entity class as a base.
 * Uses the MovementHandler class to handle autonomous movement around the
 * map, with random direction choices. It will interact with other rats by
 * murdering them mercilessly
 *
 * @author Maksim Samokhvalov
 * @version 0.3
 * Copyright: N/A
 */

public class DeathRat extends Item {

    /**
     * Death Rat explode image resource.
     */
    private static final URL DEATH_RAT_IMAGE
            = DeathRat.class.getResource("assets/DeathRat.png");

    /**
     * Maximum and minimum number of rats the death rat will kill before dying.
     */
    private static final int MAX_KILL_COUNT = 5;

    /**
     * The first 8 updates for the DeathRat are its stationary time. Where it
     * will do nothing in this state.
     */
    private static final int STATIONARY_TIME = 8;

    /**
     * The damage to deal to a no entry sign.
     */
    private static final int NO_ENTRY_DAMAGE = 25;

    /**
     * Movement handler object that will manage the rats movements
     * abstracting the complications with the actual movements.
     */
    private final MovementHandler movementHandler;

    /**
     * The number of rats the death rat has to kill before dying.
     */
    private int killsRemaining;

    /**
     * The number of ticks the death rat has left in its stationary state.
     */
    private int variableStationaryTime;

    /**
     * Builds a death rat object from the provided args string.
     *
     * @param args Arguments used to build a bomb.
     * @return Newly constructed Bomb.
     */
    public static DeathRat build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = 5;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[0]);
            final int col = Integer.parseInt(args[1]);
            final int health = Integer.parseInt(args[2]);
            final int remainingKills = Integer.parseInt(args[3]);
            final int stationaryTime = Integer.parseInt(args[4]);

            return new DeathRat(row,
                    col,
                    health,
                    remainingKills,
                    stationaryTime
            );
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
    public DeathRat(final int initRow,
                    final int initCol) {
        super(initRow, initCol);

        // Fixed number of kills the death rat has
        this.killsRemaining = MAX_KILL_COUNT;
        this.variableStationaryTime = STATIONARY_TIME;

        this.movementHandler = new MovementHandler(
                this,
                MovementHandler.getAsList(Grass.class, Tunnel.class),
                MovementHandler.getAsList(NoEntry.class)
        );
        final List<CardinalDirection> directions = new ArrayList<>(
                Arrays.stream(CardinalDirection.values()).toList()
        );
        Collections.shuffle(directions);
        this.movementHandler.setDirectionOrder(
                directions.toArray(new CardinalDirection[0])
        );
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public DeathRat(final int initialRow,
                    final int initialCol,
                    final int curHealth) {
        super(initialRow, initialCol, curHealth);

        // Set the default number of kills the death rat can have
        this.killsRemaining = MAX_KILL_COUNT;
        this.variableStationaryTime = STATIONARY_TIME;

        this.movementHandler = new MovementHandler(
                this,
                MovementHandler.getAsList(Grass.class, Tunnel.class),
                MovementHandler.getAsList(NoEntry.class)
        );
        final List<CardinalDirection> directions = new ArrayList<>(
                Arrays.stream(CardinalDirection.values()).toList()
        );
        Collections.shuffle(directions);
        this.movementHandler.setDirectionOrder(
                directions.toArray(new CardinalDirection[0])
        );
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public DeathRat(final int initialRow,
                    final int initialCol,
                    final int curHealth,
                    final int killsRemaining,
                    final int variableStationaryTime) {
        super(initialRow, initialCol, curHealth);

        // Death rat variable states
        this.killsRemaining = killsRemaining;
        this.variableStationaryTime = variableStationaryTime;

        this.movementHandler = new MovementHandler(
                this,
                MovementHandler.getAsList(Grass.class, Tunnel.class),
                MovementHandler.getAsList(NoEntry.class)
        );
        final List<CardinalDirection> directions = new ArrayList<>(
                Arrays.stream(CardinalDirection.values()).toList()
        );
        Collections.shuffle(directions);
        this.movementHandler.setDirectionOrder(
                directions.toArray(new CardinalDirection[0])
        );
    }

    /**
     * @return Remaining number of kills before the death rat dies.
     */
    public int getKillsRemaining() {
        return killsRemaining;
    }

    /**
     * Place where this rat can be updated and, do something once provided
     * some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this entity.
     * @implNote Both Objects are Object because we don't have
     * implementations for these objects just yet.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {

        // If not time to move, don't.
        if (variableStationaryTime > 0) {
            variableStationaryTime--;
            return;
        }

        // Kill the death rat if no more usages
        if (killsRemaining <= 0) {
            this.kill();
            return;
        }

        // Make a move onto a tile
        final Optional<MovementResult> optResult
                = movementHandler.makeMove(contextMap);

        if (optResult.isPresent()) {
            final MovementResult result = optResult.get();

            // If blocked by a blacklisted entity; damage the entity.
            if (result.wasBlocked()) {
                final Entity e = result.getEntitiesThatBlocked()[0];
                if (e instanceof NoEntry) {
                    ((NoEntry) e).damage(NO_ENTRY_DAMAGE);
                }

                // Not blocked means move to that tile
            } else {
                // Get possible entities to kill
                final Entity[] entOnFrom = result.getEntitiesOnFromPos();
                final Entity[] entOnTo = result.getEntitiesOnToPos();

                final TileData toPosition = result.getToPosition();

                this.setRow(toPosition.getRow());
                this.setCol(toPosition.getCol());

                // Inform gui of new position
                this.fireEvent(new EntityMovedEvent(
                        this,
                        result.getFromPosition().getRow(),
                        result.getFromPosition().getCol(),
                        0
                ));

                // Attempt to kill a rat on the current position
                for (Entity e : entOnFrom) {
                    if (e instanceof Rat) {
                        e.kill();
                        this.killsRemaining--;
                        return;
                    }
                }
                // Attempt to kill a rat on the moving to position
                for (Entity e : entOnTo) {
                    if (e instanceof Rat) {
                        e.kill();
                        this.killsRemaining--;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    @Override
    public URL getDisplaySprite() {
        return DEATH_RAT_IMAGE;
    }

    /**
     * Build the Rat to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Rat class.
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        return String.format(
                "[DeathRat, [%s, %s, %s, %s, %s], []]",
                getRow(),
                getCol(),
                getHealth(),
                getKillsRemaining(),
                this.variableStationaryTime
        );
    }

    /**
     * Kills the Death Rat.
     */
    @Override
    public void kill() {
        super.kill();
        this.fireEvent(new EntityDeathEvent(
                this,
                null,
                null
        ));
    }
}
