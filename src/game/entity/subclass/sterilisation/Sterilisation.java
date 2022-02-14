package game.entity.subclass.sterilisation;

import game.RatGame;
import game.classinfo.tags.DisplaySpriteResource;
import game.classinfo.tags.TargetConstructor;
import game.classinfo.tags.WritableField;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.rat.Rat;
import game.event.impl.entity.specific.general.EntityDeOccupyTileEvent;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityOccupyTileEvent;
import game.event.impl.entity.specific.general.GenericAudioEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.Tile;
import gui.game.EventAudio.GameAudio;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sterilisation.java - A sterilisation item.
 * Uses the Entity class as a base.
 * Once placed, for a certain amount of time, all rats within a certain
 * radius will be made fertile.
 *
 * @author Morgan Gardner
 * @version 0.3
 * Copyright: N/A
 */

public class Sterilisation extends Item {

    /**
     * Sterilisation item image resource.
     */
    @DisplaySpriteResource
    private static final URL STERILISATION_IMAGE
            = Sterilisation.class.getResource("assets/Sterilisation.png");

    /**
     * Sterilisation affected area image resource.
     */
    private static final URL STERILISATION_AREA
            = Sterilisation.class.getResource("assets/SterilisationAOE.png");

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
     * Expected arguments number for arguments array in build.
     */
    private static final int EXPECTED_ARGUMENTS_NUMBER = 4;

    /**
     * Number that Sterilisation area is going to be multiplied by.
     */
    private static final int IMAGE_SIZE_MULTIPLICATION = 4;

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
    @WritableField(name = "Length of Sterilisation AOE",
            defaultValue = "" + DURATION_TIME)
    private int currentTime;

    /**
     * Builds a Sterilisation object from the provided args string.
     *
     * @param args Arguments used to build a Sterilisation.
     * @return Newly constructed Sterilisation.
     * @throws ImproperlyFormattedArgs if the String can not be parsed.
     * @throws InvalidArgsContent      if the arguments are not formatted correctly.
     */
    public static Sterilisation build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = EXPECTED_ARGUMENTS_NUMBER;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[ROW_INDEX]);
            final int col = Integer.parseInt(args[COL_INDEX]);
            final int health = Integer.parseInt(args[HEALTH_INDEX]);
            final int currentTime = Integer.parseInt(args[CURRENT_TIME_INDEX]);

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
    @TargetConstructor
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
     * Draws the tile to the map. Sterilise rats on the affected tiles.
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

        final int updateTimeFrame = 1000;
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
                    final int displayTimeMs = 500;
                    Thread.sleep(displayTimeMs);

                    // Place sterilise effect sprite
                    this.fireEvent(new EntityOccupyTileEvent(
                            this,
                            getRow(),
                            getCol(),
                            0,
                            STERILISATION_AREA,
                            null,
                            Tile.DEFAULT_SIZE * IMAGE_SIZE_MULTIPLICATION
                    ));

                    this.fireEvent(new GenericAudioEvent(
                            this,
                            GameAudio.IRRADIATE.getResource()
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

    /**
     * Sterilise rats on tiles affected by Sterilisation.
     *
     * @param contextMap The map containing additional information about the
     *                   map.
     */
    private void sterilise(final ContextualMap contextMap) {
        tilesToSterilise.forEach(tile -> {
            //Make all rats occupying the entities sterile
            for (Entity entity : tile.getEntities()) {
                if (entity instanceof Rat) {
                    ((Rat) entity).setIsFertile(false);
                    System.out.printf("Rat on position %dx%d (sex = %s), " +
                                    "isFertile " +
                            "changed to false.%n", ((Rat) entity).getRow(),
                            ((Rat) entity).getCol(), ((Rat) entity).getSex().toString());
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
     * Build the Sterilisation item to a String that can be saved to a File;
     * returns all required arguments to restore it in a string.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Sterilisation class.
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
     * @param contextMap The contextual map containing information about the
     *                   map.
     */
    private void initializeTilesOccupied(final ContextualMap contextMap) {
        tilesToSterilise = contextMap.getAdjacentTiles(
                contextMap.getOriginTile(this)
        );
    }

    /**
     * Makes the Sterilisation disappear.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Sterilisation class.
     */
    private void deOccupy(final ContextualMap contextMap) {
        this.fireEvent(new EntityDeOccupyTileEvent(
                this,
                getRow(),
                getCol()
        ));
    }
}
