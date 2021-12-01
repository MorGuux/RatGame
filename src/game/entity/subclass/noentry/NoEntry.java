package game.entity.subclass.noentry;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.entity.Item;
import game.event.impl.entity.specific.general.SpriteChangeEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import java.net.URL;
import java.util.Arrays;

/**
 * No Entry Item blocks any Entities and Items from moving onto the same tile
 * as it. Persists until enough interactions have occurred which then it will
 * no longer persist.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class NoEntry extends Item {

    /**
     * No entry sign's Stage 1 health - when 3 rats have collided.
     */
    private static final int STAGE_1_HEALTH = 25;

    /**
     * No entry sign's Stage 2 health - when 2 rats have collided.
     */
    private static final int STAGE_2_HEALTH = 50;

    /**
     * No entry sign's Stage 3 health - when 1 rat has collided.
     */
    private static final int STAGE_3_HEALTH = 75;

    /**
     * No entry sign's Stage 4 health - when no rats have collided.
     */
    private static final int STAGE_4_HEALTH = 100;


    /**
     * No entry sign resource at 25 health (stage 1).
     */
    private static final URL NO_ENTRY_STAGE_1
            = NoEntry.class.getResource("assets/NoEntrySign_3.png");

    /**
     * No entry sign resource at 50 health (stage 2).
     */
    private static final URL NO_ENTRY_STAGE_2
            = NoEntry.class.getResource("assets/NoEntrySign_2.png");

    /**
     * No entry sign resource at 75 health (stage 3).
     */
    private static final URL NO_ENTRY_STAGE_3
            = NoEntry.class.getResource("assets/NoEntrySign_1.png");

    /**
     * No entry sign resource at 100 health (stage 4).
     */
    private static final URL NO_ENTRY_STAGE_4
            = NoEntry.class.getResource("assets/NoEntrySign.png");


    /**
     * Builds a NoEntry object from the provided args string.
     *
     * @param args Arguments used to build a no entry sign.
     * @return Newly constructed no entry sign.
     */
    public static NoEntry build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = 3;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[0]);
            final int col = Integer.parseInt(args[1]);
            final int health = Integer.parseInt(args[2]);

            return new NoEntry(row, col, health);
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
    public NoEntry(final int initRow,
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
    public NoEntry(final int initialRow,
                   final int initialCol,
                   final int curHealth) {
        super(initialRow, initialCol, curHealth);
    }

    /**
     * Place where this entity can be updated and, do something once provided
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

    }

    /**
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    @Override
    public URL getDisplaySprite() {
        return switch (this.getHealth()) {
            case STAGE_1_HEALTH -> NO_ENTRY_STAGE_1;
            case STAGE_2_HEALTH -> NO_ENTRY_STAGE_2;
            case STAGE_3_HEALTH -> NO_ENTRY_STAGE_3;
            case STAGE_4_HEALTH -> NO_ENTRY_STAGE_4;
            default -> null;
        };
    }

    /**
     * Build the NoEntry to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Entity class.
     * @return String or args which can be used to construct this specific
     * state of the Object.
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        return String.format("[NoEntry,[%d,%d,%d],[]]",
                this.getRow(), this.getCol(), this.getHealth());
    }

    /**
     * Damages an Entity by the provided amount. Unless the damage is fatal
     * in which then it will just {@link #kill()} the Entity instead.
     *
     * @param damage The amount of damage to deal to the Entity.
     */
    @Override
    public void damage(final int damage) {
        super.damage(damage);
        this.fireEvent(new SpriteChangeEvent(this, 0,
                getDisplaySprite()));
    }
}
