package game.entity.subclass.poison;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.rat.Rat;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Poison.java - A poison item.
 * Uses the Entity class as a base.
 * Upon contact by a Rat, it will poison the Rat, killing it. It will then be
 * removed from the game.
 *
 * @author Morgan Gardner
 * @version 0.2
 * Copyright: N/A
 */

public class Poison extends Item {

    /**
     * Poison image resource.
     */
    private static final URL POISON_IMAGE
            = Poison.class.getResource("assets/Poison.png");

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
     * Expected arguments number for arguments array in build.
     */
    private static final int EXPECTED_ARGUMENTS_NUMBER = 3;

    /**
     * Builds a Poison object from the provided args string.
     *
     * @param args Arguments used to build a poison.
     * @return Newly constructed Poison.
     * @throws ImproperlyFormattedArgs if the String can not be parsed.
     * @throws InvalidArgsContent if the arguments are not formatted correctly.
     */
    public static Poison build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = EXPECTED_ARGUMENTS_NUMBER;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[ROW_INDEX]);
            final int col = Integer.parseInt(args[COL_INDEX]);
            final int health = Integer.parseInt(args[HEALTH_INDEX]);

            return new Poison(row, col, health);
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
    public Poison(final int initRow,
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
    public Poison(final int initialRow,
                  final int initialCol,
                  final int curHealth) {
        super(initialRow, initialCol, curHealth);
    }

    /**
     * Kills one of rats standing on the same tile if any.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this Poison item.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        final Entity[] entities
                = contextMap.getOriginTile(this).getEntities();

        final Stream<Entity> ratStream =
                Arrays.stream(entities).filter(i -> i instanceof Rat);

        // Kill one random rat, then itself
        final List<Entity> rats = ratStream.toList();

        // If one or more rats; kill a random rat then itself.
        if (rats.size() > 0) {
            final Random r = new Random();
            rats.get(r.nextInt(rats.size())).kill();
            this.fireEvent(new EntityDeathEvent(
                    this,
                    null,
                    null
            ));
            this.kill();
        }
    }

    /**
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    @Override
    public URL getDisplaySprite() {
        return POISON_IMAGE;
    }

    /**
     * Build the Poison item to a String that can be saved to a File;
     * returns all required arguments to restore it in a string.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Poison class.
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        return String.format(
                "[Poison, [%s,%s,%s], []]",
                getRow(),
                getCol(),
                getHealth()
        );
    }
}
