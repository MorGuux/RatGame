package game.entity.subclass.sterilisation;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.bomb.Bomb;
import game.entity.subclass.rat.Rat;
import game.event.impl.entity.specific.general.EntityOccupyTileEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sterilisation.java - A sterilisation item.
 * Uses the Entity class as a base.
 * Once placed, after a certain amount of time, all rats within a certain
 * radius will be inhibited from breeding for a duration of time.
 *
 * @author Morgan Gardner
 * @version 0.1
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
        = Bomb.class.getResource("assets/Explosion.png");
        //= Sterilisation.class.getResource("assets/Sterilisation.png");

    /**
     * Sterilisation affected area image resource number 2 (more transparent).
     */
    private static final URL STERILISATION_AREA_2
        = Bomb.class.getResource("assets/Explosion.png");
        //= Sterilisation.class.getResource("assets/Sterilisation.png");
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
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
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
     * Returns current sterilisation time until the end.
     * @return timer value indicating end of the item
     */
    public int getCurrentTime() {
        return this.currentTime;
    }

    /**
     * Modify time until the end of sterilisation duration.
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
        //TODO : Implement sterilisation update. Will request all rats within
        // a radius of this item and sterilise them (set isFertile to false)
        // after a set duration.
        //TODO replace 300 with RatGame.UPDATE_TIME_FRAME
        this.setCurrentTime(this.getCurrentTime() - 300);
        System.out.println("Sterilisation time: " + currentTime);

        if (this.getCurrentTime() > 0) {
            this.sterilise(contextMap, this.getCurrentTime());
        } else {
            this.kill();
        }
    }

    private void sterilise(final ContextualMap contextMap, final int time) {
        List<TileData> tiles = new ArrayList<>();


        //get surrounding tiles
        tiles.add(contextMap.getTileDataAt(this.getRow() - 1, this.getCol()));
        tiles.add(contextMap.getTileDataAt(this.getRow() + 1, this.getCol()));
        tiles.add(contextMap.getTileDataAt(this.getRow(), this.getCol() - 1));
        tiles.add(contextMap.getTileDataAt(this.getRow(), this.getCol() + 1));
        tiles.add(contextMap.getTileDataAt(this.getRow() - 1,
                this.getCol() - 1));
        tiles.add(contextMap.getTileDataAt(this.getRow() + 1,
                this.getCol() - 1));
        tiles.add(contextMap.getTileDataAt(this.getRow() - 1,
                this.getCol() + 1));
        tiles.add(contextMap.getTileDataAt(this.getRow() + 1,
                this.getCol() + 1));

        final URL sprite;
        //make sprite different every tick
        //todo replace 600 with 2*UPDATE_TIME_FRAME
        if (time % 600 == 0) {
            sprite = STERILISATION_AREA;
        } else {
            sprite = STERILISATION_AREA_2;
        }

        tiles.forEach(tile -> {
            this.fireEvent(new EntityOccupyTileEvent(
                    this,
                    tile.getRow(),
                    tile.getCol(),
                    0,
                    sprite,
                    null));

            //Make all rats occupying the entities sterile
            for (Entity entity : tile.getEntities()) {
                if (entity instanceof Rat) {
                    ((Rat) entity).makeFertile();
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
        return String.format("[Sterilisation, [%d, %d, %d, %d], []]",
                this.getRow(), this.getCol(), this.getHealth(),
                this.getCurrentTime());
    }
}
