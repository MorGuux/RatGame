package game.entity.subclass.sterilisation;

import game.RatGame;
import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.rat.Rat;
import game.event.impl.entity.specific.general.EntityDeOccupyTileEvent;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityOccupyTileEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.Tile;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sterilisation.java - A sterilisation item.
 * Uses the Entity class as a base.
 * Once placed, after a certain amount of time, all rats within a certain
 * radius will be inhibited from breeding for a duration of time.
 *
 * @author Morgan Gardner
 * @version 0.3
 * Copyright: N/A
 */

public class Sterilisation extends Item {

    /**
     * Sterilisation item image resource.
     */
    private static final URL STERILISATION_IMAGE
            = Sterilisation.class.getResource("assets/Sterilisation.png");

    /**
     * Sterilisation affected area image resource.
     */
    private static final URL STERILISATION_AREA
            = Sterilisation.class.getResource("assets/SterilisationAOE.png");

    /**
     * Thread service that will handle executing tasks one after another on a
     * different thread. Employs a queue system so tasks are executed in
     * order of submission.
     */
    private final ExecutorService threadService =
            Executors.newFixedThreadPool(1);

    /**
     * Time in milliseconds sterilisation is active.
     */
    private static final int DURATION_TIME = 3_000;

    /**
     * Current time before sterilisation is off.
     */
    private int currentTime;

    /**
     * Builds a Sterilisation object from the provided args string.
     *
     * @param args Arguments used to build a bomb.
     * @return Newly constructed Bomb.
     */
    public static Sterilisation build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = 4;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[0]);
            final int col = Integer.parseInt(args[1]);
            final int health = Integer.parseInt(args[2]);
            final int currentTime = Integer.parseInt(args[3]);

            return new Sterilisation(row, col, health, currentTime);
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
    public Sterilisation(final int initRow,
                         final int initCol) {
        super(initRow, initCol);
        this.currentTime = DURATION_TIME;
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public Sterilisation(final int initialRow,
                         final int initialCol,
                         final int curHealth) {
        super(initialRow, initialCol, curHealth);
        this.currentTime = DURATION_TIME;
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow  Row in a 2D Array. A[ROW][COL]
     * @param initialCol  Col in a 2D Array. A[ROW][COL]
     * @param curHealth   Current health of the Entity.
     * @param currentTime Current time until the end of Sterilisation
     */
    public Sterilisation(final int initialRow,
                         final int initialCol,
                         final int curHealth,
                         final int currentTime) {
        super(initialRow, initialCol, curHealth);
        this.currentTime = currentTime;
    }

    /**
     * List of tiles that Sterilisation is affecting.
     */
    private List<TileData> tilesToSterilise;

    /**
     * Returns current sterilisation time until the end.
     *
     * @return timer value indicating end of the item
     */
    public int getCurrentTime() {
        return this.currentTime;
    }

    /**
     * Modify time until the end of sterilisation duration.
     *
     * @param currentTime timer value indicating end of the item
     */
    public void setCurrentTime(final int currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * Place where this Sterilisation item can be updated and, do something once
     * provided some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this Sterilisation item.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        if (tilesToSterilise == null) {
            this.initializeTilesOccupied(contextMap);
        }

        // Use constants based on the Sterilisation class for time units,
        // completely ignore the games update time state. As it is not
        // relevant to any entity. Using these values I can guarantee that
        // there will be 6 pulses (3000 / 500) and each pulse take
        // approximately 500 ms.

        final int updateTimeFrame = 500;
        this.setCurrentTime(
                this.getCurrentTime() - updateTimeFrame
        );

        if (this.getCurrentTime() >= 0) {
            // Sterilise entities
            this.sterilise(contextMap);

            // Visual effect over time an executor service will guarantee that
            // the task will finish before another one starts.
            threadService.submit(() -> {

                try {
                    // Delay the display (this stops the display and remove
                    // overlapping)
                    final int displayTimeMs = 250;
                    Thread.sleep(displayTimeMs);

                    // Place sterilise effect sprite
                    this.fireEvent(new EntityOccupyTileEvent(
                            this,
                            getRow(),
                            getCol(),
                            0,
                            STERILISATION_AREA,
                            null,
                            Tile.DEFAULT_SIZE * 4
                    ));

                    // Let the sprite display for some time
                    Thread.sleep(displayTimeMs);

                    // Remove the sprite afterwards.
                    this.deOccupy(contextMap);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });


        } else {
            this.kill();

            // At the end of the execution (once all the Pulses have been
            // displayed) the Entity can be removed then.
            this.threadService.submit(() -> {
                this.fireEvent(new EntityDeathEvent(
                        this,
                        null,
                        null
                ));
            });
            // Shutdown the service to inform of no more tasks
            this.threadService.shutdown();
        }
    }

    // todo comment this Jakub
    /**
     *
     * @param contextMap
     */
    private void sterilise(final ContextualMap contextMap) {
        tilesToSterilise.forEach(tile -> {
            //Make all rats occupying the entities sterile
            for (Entity entity : tile.getEntities()) {
                if (entity instanceof Rat) {
                    ((Rat) entity).setIsFertile(true);
                }
            }
        });
    }

    /**
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    @Override
    public URL getDisplaySprite() {
        return STERILISATION_IMAGE;
    }

    /**
     * Build the Sterilisation item to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Poison class.
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        return String.format(
                "[Sterilisation, [%d, %d, %d, %d], []]",
                this.getRow(),
                this.getCol(),
                this.getHealth(),
                this.getCurrentTime()
        );
    }

    /**
     * Initializes the list of tiles affected by Sterilisation.
     *
     * @param contextMap The contextual map containing information about map.
     */
    private void initializeTilesOccupied(final ContextualMap contextMap) {
        tilesToSterilise = contextMap.getAdjacentTiles(
                contextMap.getOriginTile(this)
        );
    }

    // todo comment this Jakub
    /**
     *
     * @param contextMap
     */
    private void deOccupy(final ContextualMap contextMap) {
        tilesToSterilise.forEach(tile -> {
            this.fireEvent(new EntityDeOccupyTileEvent(
                    this,
                    tile.getRow(),
                    tile.getCol())
            );
        });

        this.fireEvent(new EntityDeathEvent(this, null, null));
    }
}
