package game.entity.subclass.sterilisation;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.entity.Item;
import game.entity.subclass.deathRat.DeathRat;
import game.entity.subclass.femaleSexChange.FemaleSexChange;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;

import javax.naming.Context;
import java.net.URL;
import java.util.Arrays;

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
     * Sterilisation explode image resource.
     */
    private static final URL STERILISATION_IMAGE
            = Sterilisation.class.getResource("/assets/Sterilisation.png");

    /**
     * Builds a Bomb object from the provided args string.
     *
     * @param args Arguments used to build a bomb.
     * @return Newly constructed Bomb.
     */

    public static Sterilisation build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = 3;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[0]);
            final int col = Integer.parseInt(args[1]);
            final int health = Integer.parseInt(args[2]);

            return new Sterilisation(row, col, health);
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
    }

    /**
     * Place where this Sterilisation item can be updated and, do something once
     * provided some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this Sterilisation item.
     * @implNote Both Objects are Object because we don't have
     * implementations for these objects just yet.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        //TODO : Implement sterilisation update. Will request all rats within
        // a radius of this item and sterilise them (set isFertile to false)
        // after a set duration.
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
     * @implNote Context map is Object since we don't have an implementation
     * of it yet.
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        return null;
    }
}
