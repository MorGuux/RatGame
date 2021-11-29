package game.entity.subclass.maleSexChange;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.entity.Item;
import game.entity.subclass.bomb.Bomb;
import game.entity.subclass.deathRat.DeathRat;
import game.entity.subclass.noentry.NoEntry;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;

import java.net.URL;
import java.util.Arrays;

/**
 * Filename -- MaleSexChange.java
 * Created -- 16/11/2021
 * Purpose -- Models the male sex change item of the Rat Game.
 * Based off the Entity class (as a template).
 * When a rat touches this entity, it will change its sex to Male.
 * It will then be removed from the game.
 * Will also be destroyed if in the radius of a bomb explosion.
 * @author Shashank Jain
 * @version 0.1
 * Copyright: N/A
 */

public class MaleSexChange extends Item {

    /**
     * Male Sex Change image resource.
     */
    private static final URL MALE_SEX_CHANGE_IMAGE
            = MaleSexChange.class.getResource("/assets/MaleSexChange.png");

    /**
     * Builds a Bomb object from the provided args string.
     *
     * @param args Arguments used to build a bomb.
     * @return Newly constructed Bomb.
     */
    public static MaleSexChange build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = 3;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[0]);
            final int col = Integer.parseInt(args[1]);
            final int health = Integer.parseInt(args[2]);

            return new MaleSexChange(row, col, health);
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
    public MaleSexChange(final int initRow,
                         final int initCol) {
        super(initRow, initCol);
    }

    /**
     * Construct an Entity from the base starting Row and Column.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public MaleSexChange(final int initRow,
                         final int initCol,
                         final int curHealth) {
        super(initRow, initCol, curHealth);
    }

    /**
     * This should be called where this item can be updated and,
     * does something once some context objects are passed here.
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this item.
     * @implNote Both Objects are Object because we don't have
     * implementations for these objects just yet.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        //TODO : Implement update method for this class.
        // Will check if a rat has made contact and if so, will change
        // its sex to male. It will then remove itself from the game.
        // Also checks if it is within a bomb explosion radius, and if so,
        // will be destroyed & removed from the game.
    }

    /**
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    @Override
    public URL getDisplaySprite() {
        return MALE_SEX_CHANGE_IMAGE;
    }

    /**
     * Builds this item to a String that can be saved to a File;
     * all parameters needed to construct the current state of the entity are
     * required.
     * @param contextMap The game context map which contains extra info that may
     * not be stored directly in this class.
     * @implNote Context map is Object since we don't have an implementation
     * of it yet.
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        return null;
    }
}
