package game.entity.subclass.rat;

import game.RatGame;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.contextmap.handler.MovementHandler;
import game.contextmap.handler.result.MovementResult;
import game.entity.Entity;
import game.entity.subclass.noentry.NoEntry;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityMovedEvent;
import game.event.impl.entity.specific.general.SpriteChangeEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.base.grass.Grass;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Rat.java - A rat entity.
 * Uses the Entity class as a base.
 * Uses the MovementHandler class to handle autonomous movement around the
 * map, with random direction choices. It will interact with other rats to
 * mate, and with items that can damage and change properties of it.
 *
 * @author Morgan Gardner
 * @version 0.4
 * Copyright: N/A
 */
public class Rat extends Entity {

    /**
     * The number of points a rat awards when killed.
     */
    private static final int BASE_POINTS = 10;

    /**
     * The damage that the rat will deal to entities.
     */
    private static final int BASE_DAMAGE = 25;

    /**
     * Each update for the rat is assumed to be under this time frame
     * regardless of what the game update timeframe is.
     */
    private static final int UPDATE_TIME_VALUE = 300;

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
     * A service for rats to queue the actions that they want to do overtime.
     * Things such as mating would lock the rats of for a set time before
     * letting them move again.
     */
    private final ExecutorService taskExecutionService
            = Executors.newFixedThreadPool(1);

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
    private final AtomicBoolean isPregnant;
    /**
     * Is the rat currently having sex. Atomic since this will be accessed by
     * Two threads.
     */
    private final AtomicBoolean isMating;

    /**
     * The number of babies that a pregnant rat has.
     */
    private int numBabies;

    /**
     * The movement handler for the Rat. This allows the rat to ignore
     * attempting to traverse around a map but instead react to the map.
     */
    private final MovementHandler movementHandler;

    /**
     * The current time (ms) left before the rat grows into an adult rat
     * (from a baby).
     */
    private int timeToAge;
    /**
     * The time in milliseconds before the rat will give birth.
     */
    private int timeTilBirth;

    /**
     * Constructs a default baby rat of a random gender.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     */
    public Rat(final int initRow,
               final int initCol) {
        super(initRow, initCol);

        Random r = new Random();
        final int minTimeToAge = 8_000;
        final int maxTimeToAge = 20_000;
        final int sexBound = 100;
        final int maleBound = 65;

        this.age = Age.BABY;
        this.sex = r.nextInt(sexBound) <= maleBound ? Sex.MALE : Sex.FEMALE;
        this.timeToAge = r.nextInt(minTimeToAge, maxTimeToAge);
        this.isFertile = true;
        this.timeTilBirth = 0;
        this.isPregnant = new AtomicBoolean();
        this.isMating = new AtomicBoolean();

        this.movementHandler = new MovementHandler(
                this,
                MovementHandler.getAsList(Grass.class),
                MovementHandler.getAsList(NoEntry.class)
        );
    }

    /**
     * Constructs a Rat from all the possible state variables that apply to a
     * rat.
     *
     * @param initialRow   The row of the rat in a map.
     * @param initialCol   The column of the rat in a map.
     * @param curHealth    The current health of the rat.
     * @param sex          The gender/sex of a rat.
     * @param age          The age of the rat.
     * @param timeToAge    How long in millis until the rat becomes an adult.
     * @param isFertile    The fertility of a rat.
     * @param isPregnant   The pregnant state of the rat.
     * @param timeTilBirth The time in milliseconds until the rat will give
     *                     birth.
     * @param isBreeding   The current breeding state of the rat.
     */
    public Rat(final int initialRow,
               final int initialCol,
               final int curHealth,
               final Sex sex,
               final Age age,
               final int timeToAge,
               final boolean isFertile,
               final boolean isPregnant,
               final int timeTilBirth,
               final boolean isBreeding) {
        super(initialRow, initialCol, curHealth);
        this.sex = sex;
        this.age = age;
        this.timeToAge = timeToAge;
        this.isFertile = isFertile;
        this.isPregnant = new AtomicBoolean(isPregnant);
        this.timeTilBirth = timeTilBirth;
        this.isMating = new AtomicBoolean(isBreeding);

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
        final int expectedArgsLength = 10;

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
            final int timeTilBirth = Integer.parseInt(args[8]);
            final boolean isBreeding = Boolean.parseBoolean(args[9]);

            return new Rat(
                    row,
                    col,
                    health,
                    sex,
                    age,
                    timeToAge,
                    isFertile,
                    isPregnant,
                    timeTilBirth,
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
        // Task running in the background
        if (this.isBreeding()) {
            return;
        }

        if (age.equals(Age.BABY)) {
            this.makeBabyMove(contextMap);

            // Update age time frame
            if (this.timeToAge > 0) {
                this.timeToAge = timeToAge - UPDATE_TIME_VALUE;

                // Make the rat an adult
            } else {
                this.timeToAge = 0;
                this.age = Age.ADULT;

                this.fireEvent(new SpriteChangeEvent(
                        this,
                        0,
                        getDisplaySprite()
                ));
            }

            return;
        }

        // Give birth
        if (this.timeTilBirth <= 0) {
            for (int i = 0; i < this.numBabies; ++i) {
                ratGame.spawnEntity(new Rat(this.getRow(), this.getCol()));
            }
            this.numBabies = 0;

        } else {
            timeTilBirth = timeTilBirth - UPDATE_TIME_VALUE;
        }

        final Optional<MovementResult> result
                = movementHandler.makeMove(contextMap);
        result.ifPresent((moveResult) -> handleMove(moveResult, contextMap));
    }

    /**
     * Wraps all the possible actions that a baby rat can do.
     *
     * @param map The contextual map that the baby rat will move around on.
     */
    private void makeBabyMove(final ContextualMap map) {
        Optional<MovementResult> result
                = this.movementHandler.makeMove(map);

        result.ifPresent((moveResult) -> handleMove(moveResult, map));
        result = this.movementHandler.makeMove(map);

        result.ifPresent((moveResult) -> handleMove(moveResult, map));
    }

    /**
     * Handles a move that a rat can make and what it should do with that move.
     *
     * @param result The result from the move.
     * @param map    The map that the move will be made on.
     */
    private void handleMove(final MovementResult result,
                            final ContextualMap map) {

        // Damage the entity that blocked it
        if (result.wasBlocked()) {
            final Entity blocker = result.getEntitiesOnToPos()[0];
            if (blocker instanceof NoEntry) {
                ((NoEntry) blocker).damage(BASE_DAMAGE);
            }

            // Interact with entities and then move to the tile
        } else {

            final TileData toPosition = result.getToPosition();

            this.setRow(toPosition.getRow());
            this.setCol(toPosition.getCol());

            this.fireEvent(new EntityMovedEvent(
                    this,
                    toPosition.getRow(),
                    toPosition.getCol(),
                    0
            ));

            map.moveToTile(this, toPosition);

            // Only adults will interact with entities
            if (this.age.equals(Age.ADULT)) {
                this.interactWithEntities(toPosition.getEntities());
            }
        }
    }

    /**
     * Interacts with one of the entities from the provided array of entities
     * based on the internal values of the rat.
     *
     * @param entities The entities to interact with.
     */
    private void interactWithEntities(final Entity[] entities) {

        // A rat of any gender can initiate sex with another rat. Both rats
        // then become locked unable to do anything until the

        final int upperBound = 100;
        final int chanceForMating = 75;
        final Sex oppositeSex
                = this.sex.equals(Sex.MALE) ? Sex.FEMALE : Sex.MALE;
        final Random r = new Random();

        for (Entity entity : entities) {

            if (entity instanceof final Rat rat) {

                final int value = r.nextInt(upperBound);

                if (value <= chanceForMating
                        && !this.isBreeding()
                        && !rat.isBreeding()
                        && rat.age.equals(Age.ADULT)
                        && rat.sex.equals(oppositeSex)
                        && !rat.isPregnant.get()
                        && !this.isPregnant.get()) {

                    this.setIsBreeding(true);
                    rat.setIsBreeding(true);

                    // Initiate sex with another rat
                    taskExecutionService.submit(() -> this.ratSex(rat, r));
                    return;
                }
            }
        }
    }

    /**
     * Initiates sex for this rat with another rat locking both rats up until
     * the sex is over.
     *
     * @param rat The rat to mate with.
     * @param r   Convenience random object for generating random rotation
     *            values.
     */
    private void ratSex(final Rat rat,
                        final Random r) {

        // TODO Implement the sex sound effects
        try {
            final int cycles = 5;
            final int fullRotation = 360;
            for (int cycle = 0; cycle < cycles; cycle++) {

                // If not shagged to death
                if (!this.isDead() && !rat.isDead()) {
                    final int sleepTime = 250;
                    Thread.sleep(sleepTime);

                    this.fireEvent(new SpriteChangeEvent(
                            this,
                            0,
                            r.nextInt(fullRotation),
                            this.getDisplaySprite()
                    ));

                    this.fireEvent(new SpriteChangeEvent(
                            rat,
                            0,
                            r.nextInt(fullRotation),
                            rat.getDisplaySprite()
                    ));
                }
            }

            this.fireEvent(new SpriteChangeEvent(
                    this,
                    0,
                    this.getDisplaySprite()
            ));

            this.fireEvent(new SpriteChangeEvent(
                    rat,
                    0,
                    rat.getDisplaySprite()
            ));

            this.setIsBreeding(false);
            rat.setIsBreeding(false);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                "[Rat, [%s,%s,%s,%s,%s,%s,%s,%s,%s,%s], []]",
                getRow(),
                getCol(),
                getHealth(),
                getSex(),
                getAge(),
                timeToAge,
                isFertile,
                isPregnant,
                timeTilBirth,
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
     * Set the rats mating state to the provided value.
     *
     * @param isBreeding The new value of the mating.
     */
    public void setIsBreeding(final boolean isBreeding) {
        this.isMating.set(isBreeding);

        if (this.sex.equals(Sex.FEMALE)) {
            final int minBabies = 1;
            final int maxBabies = 3;
            final int minTimeTilBirth = 20_000;
            final int maxTimeTilBirth = 60_000;
            final Random r = new Random();
            this.numBabies = r.nextInt(minBabies, maxBabies);
            this.timeTilBirth = r.nextInt(minTimeTilBirth, maxTimeTilBirth);
            this.isPregnant.set(true);
        }
    }

    /**
     * @return {@code true} if the rat is currently being mated with.
     * Otherwise, if not then {@code false} is returned.
     */
    public boolean isBreeding() {
        return this.isMating.get();
    }

    /**
     * Returns the age of the rat.
     *
     * @return The current age of the rat
     */
    public Age getAge() {
        return age;
    }

    // todo Does this cause a miscarriage?
    /**
     * Sets the fertile state of the rat to the provided state.
     *
     * @param isFertile The new state for fertility.
     */
    public void setIsFertile(final boolean isFertile) {
        this.isFertile = isFertile;
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
    }

    /**
     * Kills the Rat.
     */
    @Override
    public void kill() {
        super.kill();

        System.out.println("KILL!");
        this.taskExecutionService.submit(() -> {
            this.fireEvent(new EntityDeathEvent(
                    this,
                    null,
                    null
            ));
        });
        taskExecutionService.shutdown();
    }

    /**
     * Get the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    public URL getDisplaySprite() {
        if ((this.getSex().equals(Sex.MALE))
                && (this.getAge().equals(Age.ADULT))) {
            return RAT_MALE_IMAGE;

        } else if ((this.getSex().equals(Sex.FEMALE))
                && (this.getAge().equals(Age.ADULT))) {
            return RAT_FEMALE_IMAGE;

        } else if (this.getAge().equals(Age.BABY)) {
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
