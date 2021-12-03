package game.entity.subclass.rat;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.contextmap.handler.MovementHandler;
import game.contextmap.handler.result.MovementResult;
import game.entity.Entity;
import game.entity.subclass.noentry.NoEntry;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.base.grass.Grass;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

/**
 * Rat.java - A rat entity.
 * Uses the Entity class as a base.
 * Uses the MovementHandler class to handle autonomous movement around the
 * map, with random direction choices. It will interact with other rats to
 * mate, and with items that can damage and change properties of it.
 *
 * @author Morgan Gardner
 * @version 0.2
 * Copyright: N/A
 */
public class Rat extends Entity {

    /**
     * The number of points a rat awards when killed.
     */
    private static final int BASE_POINTS = 10;

    /**
     * Male rat image resource.
     */
    private static final URL RAT_MALE_IMAGE
            = Rat.class.getResource("assets/MaleRat.png");

    /**
     * Female rat image resource.
     */
    private static final URL RAT_FEMALE_IMAGE
            = Rat.class.getResource("assets/FemaleRat.png");

    /**
     * Baby rat image resource.
     */
    private static final URL RAT_BABY_IMAGE
            = Rat.class.getResource("assets/BabyRat.png");

    /**
     * Represents the sex of a rat.
     */
    public enum Sex {
        /**
         * Male rat. It can mate with other female rats.
         */
        MALE,
        /**
         * Female rat. It can mate with other male rats and give birth once
         * fertile for a set amount of time.
         */
        FEMALE
    }

    /**
     * Represents the age of the rat.
     */
    public enum Age {
        /**
         * Baby rat. It can grow into an adult after a set amount of time has
         * passed.
         */
        BABY,
        /**
         * Adult rat. Its sprite size is increased, and it can mate with
         * other rats.
         */
        ADULT
    }

    /**
     * The current sex of the rat (male/female).
     */
    private Sex sex;

    /**
     * The current age of the rat (baby/adult).
     */
    private Age age;

    /**
     * If the rat can produce offspring (give birth).
     */
    private boolean isFertile;

    /**
     * Is the rat pregnant.
     */
    private boolean isPregnant;

    /**
     * The number of babies that a pregnant rat has.
     */
    private int numBabies;

    /**
     *
     */
    private final MovementHandler movementHandler;

    /**
     * The current time (ms) left before the rat grows into an adult rat
     * (from a baby).
     */
    private int timeToAge;
    /**
     * Is the rat currently having sex.
     */
    private boolean isMating;

    /**
     * Constructs a default male rat from the base starting Row and Column.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     */
    public Rat(final int initRow,
               final int initCol) {
        super(initRow, initCol);

        this.age = Age.ADULT;
        this.sex = Sex.MALE;
        this.movementHandler = new MovementHandler(
                this,
                MovementHandler.getAsList(Grass.class),
                MovementHandler.getAsList(NoEntry.class)
        );
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public Rat(final int initialRow,
               final int initialCol,
               final int curHealth,
               final Sex sex,
               final Age age,
               final int timeToAge,
               final boolean isFertile,
               final boolean isPregnant,
               final boolean isBreeding) {
        super(initialRow, initialCol, curHealth);
        this.sex = sex;
        this.age = age;
        this.timeToAge = timeToAge;
        this.isFertile = isFertile;
        this.isPregnant = isPregnant;

        this.movementHandler = new MovementHandler(
                this,
                MovementHandler.getAsList(Grass.class),
                MovementHandler.getAsList(NoEntry.class)
        );
    }

    /**
     * Builds a Rat object from the provided args string.
     *
     * @param args Arguments used to build a rat.
     * @return Newly constructed rat.
     */
    public static Rat build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = 9;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[0]);
            final int col = Integer.parseInt(args[1]);
            final int health = Integer.parseInt(args[2]);
            final Sex sex = Sex.valueOf(args[3]);
            final Age age = Age.valueOf(args[4]);
            final int timeToAge = Integer.parseInt(args[5]);
            final boolean isFertile = Boolean.parseBoolean(args[6]);
            final boolean isPregnant = Boolean.parseBoolean(args[7]);
            final boolean isBreeding = Boolean.parseBoolean(args[8]);

            return new Rat(
                    row,
                    col,
                    health,
                    sex,
                    age,
                    timeToAge,
                    isFertile,
                    isPregnant,
                    isBreeding
            );
        } catch (Exception e) {
            throw new InvalidArgsContent(Arrays.deepToString(args));
        }
    }

    /**
     * Place where this rat can be updated and, do something once provided
     * some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this entity.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        Optional<MovementResult> result = movementHandler.makeMove(contextMap);
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
                "[Rat, [%s,%s,%s,%s,%s,%s,%s,%s,%s], []]",
                getRow(),
                getCol(),
                getHealth(),
                getSex(),
                getAge(),
                timeToAge,
                isFertile,
                isPregnant,
                isMating
        );
    }

    /**
     * Returns the sex of the rat.
     *
     * @return The current sex of the rat
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * Sets the sex of the rat to a given Sex.
     *
     * @param newSex The new sex of the rat.
     */
    public void setSex(final Sex newSex) {
        this.sex = newSex;
    }

    /**
     * Returns the age of the rat.
     *
     * @return The current age of the rat
     */
    public Age getAge() {
        return age;
    }

    /**
     * Makes Rat fertile
     */
    public void setIsFertile(boolean isFertile) {
        this.isFertile = isFertile;
    }

    //todo update comment?

    /**
     * Damages an Entity by the provided amount. Unless the damage is fatal
     * in which then it will just {@link #kill()} the Entity instead.
     *
     * @param damage The amount of damage to deal to the Entity.
     */
    @Override
    public void damage(final int damage) {
        super.damage(damage);
    }

    /**
     * Kills the Rat.
     */
    @Override
    public void kill() {
        super.kill();
    }

    /**
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    public URL getDisplaySprite() {
        if ((this.getSex() == Sex.MALE)
                && (this.getAge() == Age.ADULT)) {
            return RAT_MALE_IMAGE;

        } else if ((this.getSex() == Sex.FEMALE)
                && (this.getAge() == Age.ADULT)) {
            return RAT_FEMALE_IMAGE;

        } else if (this.getAge() == Age.BABY) {
            return RAT_BABY_IMAGE;

        } else {
            throw new RuntimeException("Validate your rats!");
        }
    }

    /**
     * @return The number of points to award when this entity is killed.
     */
    @Override
    public int getDeathPoints() {
        // +1 accounts for itself
        return BASE_POINTS * (numBabies + 1);
    }

    /**
     * Returns information about Rat hostility. Since player aims to kill the
     * rats, the rat entity is considered hostile.
     *
     * @return true
     */
    @Override
    public boolean isHostile() {
        return true;
    }
}
