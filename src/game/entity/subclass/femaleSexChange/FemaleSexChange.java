package game.entity.subclass.femaleSexChange;

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

/**
 * FemaleSexChange.java - FemaleSexChange item
 * When a rat touches this entity, it will change its sex to Female.
 * It will then be removed from the game.
 * Will also be destroyed if in the radius of a bomb explosion.
 *
 * @author Jakub Wozny
 * @version 0.2
 * Copyright: N/A
 */

public class FemaleSexChange extends Item {

    /**
     * Female Sex Change image resource.
     */
    private static final URL FEMALE_SEX_CHANGE_IMAGE
            = FemaleSexChange
            .class.getResource("assets/FemaleSexChange.png");

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
     * Builds a FemaleSexChange object from the provided args string.
     *
     * @param args Arguments used to build a FemaleSexChange.
     * @return Newly constructed FemaleSexChange.
     */
    public static FemaleSexChange build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = EXPECTED_ARGUMENTS_NUMBER;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[ROW_INDEX]);
            final int col = Integer.parseInt(args[COL_INDEX]);
            final int health = Integer.parseInt(args[HEALTH_INDEX]);

            return new FemaleSexChange(row, col, health);
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
    public FemaleSexChange(final int initRow,
                           final int initCol) {
        super(initRow, initCol);
    }

    /**
     * Construct an Entity from the base starting Row, Column and Health.
     *
     * @param initRow   Row in a 2D Array. A[ROW][COL]
     * @param initCol   Col in a 2D Array. A[ROW][COL]
     * @param curHealth Current health of the Entity.
     */
    public FemaleSexChange(final int initRow,
                           final int initCol,
                           final int curHealth) {
        super(initRow, initCol, curHealth);
    }

    /**
     * Changes the sex of one of rats standing on the same tile if any.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this item.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        final Entity[] entities = contextMap.getTileDataAt(
                this.getRow(), this.getCol()).getEntities();

        for (Entity e : entities) {
            if (e instanceof Rat) {

                final Rat.Sex prevSex = ((Rat) e).getSex();
                ((Rat) e).setSex(Rat.Sex.FEMALE);

                if (prevSex.equals(Rat.Sex.MALE)) {
                    ratGame.stateEntityUpdated(Rat.Sex.MALE, (Rat) e);
                }

                this.kill();
                return;
            }
        }
    }

    /**
     * Convenience method to kill this Entity.
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

    /**
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    @Override
    public URL getDisplaySprite() {
        return FEMALE_SEX_CHANGE_IMAGE;
    }

    /**
     * Builds this item to a String that can be saved to a File;
     * all parameters needed to construct the current state of the entity are
     * required.
     *
     * @param contextMap The game context map which contains extra info that may
     *                   not be stored directly in this class.
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        return String.format(
                "[FemaleSexChange, [%s,%s,%s], []]",
                getRow(),
                getCol(),
                getHealth()
        );
    }
}
