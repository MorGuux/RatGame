package game.entity.subclass.rat;

import game.RatGame;
import game.classinfo.tags.DisplaySpriteResource;
import game.classinfo.tags.TargetConstructor;
import game.classinfo.tags.WritableField;
import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.contextmap.handler.MovementHandler;
import game.contextmap.handler.result.MovementResult;
import game.entity.Entity;
import game.entity.subclass.noentry.NoEntry;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityMovedEvent;
import game.event.impl.entity.specific.general.GenericAudioEvent;
import game.event.impl.entity.specific.general.SpriteChangeEvent;
import game.event.impl.entity.specific.load.EntityLoadEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.Tile;
import game.tile.base.grass.Grass;
import game.tile.base.path.Path;
import game.tile.base.tunnel.Tunnel;
import gui.game.EventAudio.GameAudio;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
 * @version 0.7
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
    @DisplaySpriteResource
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
    @WritableField(name = "Sex", defaultValue = "Male")
    private Sex sex;

    /**
     * The current age of the rat (baby/adult).
     */
    @WritableField(name = "Age", defaultValue = "Baby")
    private Age age;

    /**
     * If the rat can produce offspring (give birth).
     */
    @WritableField(name = "Is Fertile?", defaultValue = "False")
    private boolean isFertile;

    /**
     * Is the rat pregnant.
     */
    @WritableField(name = "Is Pregnant?", defaultValue = "False")
    private final AtomicBoolean isPregnant;

    /**
     * Is the rat currently having sex. Atomic since this will be accessed by
     * Two threads.
     */
    @WritableField(name = "Is Mating?", defaultValue = "False")
    private final AtomicBoolean isMating;

    /**
     * States whether the rat is currently in the mating animation. This is a
     * debug value used when loading from a file to ensure that the mating
     * process will restart if the game was saved whilst they were mating.
     */
    @WritableField(name = "Is Currently Mating?", defaultValue = "False")
    private final AtomicBoolean isInMatingAnimation;

    /**
     * The number of babies that a pregnant rat has.
     */
    @WritableField(name = "Number of babies", defaultValue = "0")
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
    @WritableField(name = "Time til Adulthood", defaultValue = "12000")
    private int timeToAge;

    /**
     * The time in milliseconds before the rat will give birth.
     */
    @WritableField(name = "Time until pregnancy", defaultValue = "0")
    private int timeTilBirth;

    /**
     * Rat build String list - expected length of argument list.
     */
    private static final int EXPECTED_ARGS_LENGTH = 10;

    /**
     * Rat build String list - first argument.
     */
    private static final int ROW_ARG_INDEX = 0;

    /**
     * Rat build String list - second argument.
     */
    private static final int COL_ARG_INDEX = 1;

    /**
     * Rat build String list - third argument.
     */
    private static final int HEALTH_ARG_INDEX = 2;

    /**
     * Rat build String list - fourth argument.
     */
    private static final int SEX_ARG_INDEX = 3;

    /**
     * Rat build String list - fifth argument.
     */
    private static final int AGE_ARG_INDEX = 4;

    /**
     * Rat build String list - sixth argument.
     */
    private static final int TIME_TO_AGE_ARG_INDEX = 5;

    /**
     * Rat build String list - seventh argument.
     */
    private static final int IS_FERTILE_ARG_INDEX = 6;

    /**
     * Rat build String list - eighth argument.
     */
    private static final int IS_PREGNANT_ARG_INDEX = 7;

    /**
     * Rat build String list - ninth argument.
     */
    private static final int TIME_TILL_BIRTH_ARG_INDEX = 8;

    /**
     * Rat build String list - tenth argument.
     */
    private static final int IS_BREEDING_ARG_INDEX = 9;

    /**
     * Constructs a default baby rat of a random gender.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     */
    @TargetConstructor
    public Rat(final int initRow,
               final int initCol) {
        super(initRow, initCol);

        final Random r = new Random();
        final int minTimeToAge = 8_000;
        final int maxTimeToAge = 20_000;
        final int sexBound = 100;
        final int maleBound = 65;

        // Defaults
        this.age = Age.BABY;
        this.sex = r.nextInt(sexBound) <= maleBound ? Sex.MALE : Sex.FEMALE;
        this.timeToAge = r.nextInt(minTimeToAge, maxTimeToAge);
        this.isFertile = true;
        this.timeTilBirth = 0;
        this.isPregnant = new AtomicBoolean();
        this.isMating = new AtomicBoolean();

        // Inits to false
        isInMatingAnimation = new AtomicBoolean();

        this.movementHandler = new MovementHandler(
                this,
                MovementHandler.getAsList(Grass.class),
                MovementHandler.getAsList(NoEntry.class)
        );

        final List<CardinalDirection> directions = new ArrayList<>(
                Arrays.stream(CardinalDirection.values()).toList()
        );
        Collections.shuffle(directions);
        this.movementHandler.setDirectionOrder(
                directions.toArray(new CardinalDirection[0])
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

        // Init defaults
        this.sex = sex;
        this.age = age;
        this.timeToAge = timeToAge;
        this.isFertile = isFertile;
        this.isPregnant = new AtomicBoolean(isPregnant);
        this.timeTilBirth = timeTilBirth;
        this.isMating = new AtomicBoolean(isBreeding);

        // Inits to false
        isInMatingAnimation = new AtomicBoolean();

        // Movement handler
        this.movementHandler = new MovementHandler(
                this,
                MovementHandler.getAsList(Grass.class),
                MovementHandler.getAsList(NoEntry.class)
        );

        final List<CardinalDirection> directions = new ArrayList<>(
                Arrays.stream(CardinalDirection.values()).toList()
        );
        Collections.shuffle(directions);
        this.movementHandler.setDirectionOrder(
                directions.toArray(new CardinalDirection[0])
        );
    }

    /**
     * Builds a Rat object from the provided args string.
     *
     * @param args Arguments used to build a rat.
     * @return Newly constructed rat.
     * @throws ImproperlyFormattedArgs if the String can not be parsed.
     * @throws InvalidArgsContent if the arguments are not formatted correctly.
     */
    public static Rat build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {

        if (args.length != EXPECTED_ARGS_LENGTH) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[ROW_ARG_INDEX]);
            final int col = Integer.parseInt(args[COL_ARG_INDEX]);
            final int health = Integer.parseInt(args[HEALTH_ARG_INDEX]);
            final Sex sex = Sex.valueOf(args[SEX_ARG_INDEX]);
            final Age age = Age.valueOf(args[AGE_ARG_INDEX]);
            final int timeToAge = Integer.parseInt(args[TIME_TO_AGE_ARG_INDEX]);
            final boolean isFertile = Boolean.parseBoolean(
                    args[IS_FERTILE_ARG_INDEX]);
            final boolean isPregnant = Boolean.parseBoolean(
                    args[IS_PREGNANT_ARG_INDEX]);
            final int timeTilBirth = Integer.parseInt(
                    args[TIME_TILL_BIRTH_ARG_INDEX]);
            final boolean isBreeding = Boolean.parseBoolean(
                    args[IS_BREEDING_ARG_INDEX]);

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
     * @param map     The map that this entity may exist on.
     * @param ratGame The game that updated this entity.
     */
    @Override
    public void update(final ContextualMap map,
                       final RatGame ratGame) {

        // Task running in the background
        if ((this.isBreeding() || this.isDead())) {

            if (this.age.equals(Age.ADULT)
                    && this.isBreeding()
                    && !this.isInMatingAnimation.get()) {
                this.isInMatingAnimation.set(true);
                this.restartSexAnimation(map.getOriginTile(this).getTile());
            }
            return;
        }

        if (age.equals(Age.BABY)) {
            this.makeBabyMove(map);

            // Update age time frame
            if (this.timeToAge > 0) {
                this.timeToAge = timeToAge - UPDATE_TIME_VALUE;

                // Make the rat an adult
            } else if (this.age.equals(Age.BABY)) {
                this.timeToAge = 0;
                this.age = Age.ADULT;
            }

        } else {

            // Give birth
            if (isPregnant.get() && this.timeTilBirth <= 0) {
                for (int i = 0; i < this.numBabies; ++i) {
                    ratGame.spawnEntity(new Rat(this.getRow(), this.getCol()));
                }
                this.numBabies = 0;
                isPregnant.set(false);

            } else if (isPregnant.get()) {
                timeTilBirth = timeTilBirth - UPDATE_TIME_VALUE;
            }

            final Optional<MovementResult> result
                    = movementHandler.makeMove(map);
            result.ifPresent((moveResult) -> handleMove(moveResult, map));
        }

    }

    /**
     * Wraps all the possible actions that a baby rat can do.
     *
     * @param map The contextual map that the baby rat will move around on.
     */
    private void makeBabyMove(final ContextualMap map) {
        taskExecutionService.submit(() -> {
            final int sleepTime = 100;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.movementHandler.makeMove(map).ifPresent(
                    (result -> this.handleMove(result, map))
            );

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.movementHandler.makeMove(map).ifPresent(
                    (result -> this.handleMove(result, map))
            );
        });
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
            final TileData fromPosition = result.getFromPosition();
            map.moveToTile(this, toPosition);

            if (toPosition.getTile() instanceof Tunnel) {
                this.fireEvent(new SpriteChangeEvent(
                        this,
                        0,
                        null
                ));
            } else {
                this.fireEvent(new SpriteChangeEvent(
                        this,
                        0,
                        getDisplaySprite()
                ));
            }

            this.setRow(toPosition.getRow());
            this.setCol(toPosition.getCol());

            this.fireEvent(new EntityMovedEvent(
                    this,
                    fromPosition.getRow(),
                    fromPosition.getCol(),
                    0
            ));


            // Only adults will interact with entities
            if (this.age.equals(Age.ADULT)) {
                this.interactWithEntities(toPosition.getEntities(), toPosition);
            }
        }
    }

    /**
     * Interacts with one of the entities from the provided array of entities
     * based on the internal values of the rat.
     *
     * @param entities The entities to interact with.
     * @param data     The tile that the rat is moving to/interacting on.
     */
    private void interactWithEntities(final Entity[] entities,
                                      final TileData data) {

        // A rat of any gender can initiate sex with another rat. Both rats
        // then become locked unable to do anything until the sex is over.

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
                    taskExecutionService.submit(() ->
                            this.ratSex(
                                    rat,
                                    r,
                                    data.getTile()
                            ));
                    return;
                }
            }
        }
    }

    /**
     * Restarts the sex animation that's played when two rats have intercourse.
     * This would only get called if the animation is not playing while this
     * rat is supposed to be mating.
     *
     * @param tile The tile that the rat is currently on.
     */
    private void restartSexAnimation(final Tile tile) {

        // Not redundant; as the setIsBreeding will make the rat pregnant if
        // it is a female; could replace with a call to the set method in the
        // constructor though.
        this.setIsBreeding(true);
        this.isInMatingAnimation.set(true);

        if (tile instanceof Path) {
            final int cycles = 5;
            final Random r = new Random();
            final int fullRotation = 360;
            final int sleepTime = 250;

            this.taskExecutionService.submit(() -> {
                for (int cycle = 0; cycle < cycles; cycle++) {
                    try {
                        Thread.sleep(sleepTime);
                        this.fireEvent(new SpriteChangeEvent(
                                this,
                                0,
                                r.nextInt(fullRotation),
                                this.getDisplaySprite()
                        ));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.setIsBreeding(false);
                this.isInMatingAnimation.set(false);
            });
        }
    }

    /**
     * Initiates sex for this rat with another rat locking both rats up until
     * the sex is over.
     *
     * @param rat  The rat to mate with.
     * @param r    Convenience random object for generating random rotation
     *             values.
     * @param tile The tile that the rats are on.
     */
    private void ratSex(final Rat rat,
                        final Random r,
                        final Tile tile) {

        try {
            final int cycles = 5;
            final int fullRotation = 360;
            this.isInMatingAnimation.set(true);
            rat.isInMatingAnimation.set(true);

            this.fireEvent(new GenericAudioEvent(
                    this,
                    GameAudio.RAT_SEX.getResource()
            ));

            for (int cycle = 0; cycle < cycles; cycle++) {

                // If not shagged to death and not on tunnel tile
                if (!this.isDead()
                        && !rat.isDead()
                        && !(tile instanceof Tunnel)) {
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

            this.setIsBreeding(false);
            rat.setIsBreeding(false);
            this.isInMatingAnimation.set(false);
            rat.isInMatingAnimation.set(false);

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

        this.fireEvent(new SpriteChangeEvent(
                this,
                0,
                getDisplaySprite()
        ));
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
            final int minTimeTilBirth = 3_500;
            final int maxTimeTilBirth = 5_000;
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

    /**
     * Sets the fertile state of the rat to the provided state.
     *
     * @param isFertile The new state for fertility.
     */
    public void setIsFertile(final boolean isFertile) {
        this.isFertile = isFertile;
    }

    /**
     * Get the isFertile variable.
     *
     * @return isFertile status of the rat.
     */
    public boolean isFertile() {
        return this.isFertile;
    }

    /**
     * Damages an Entity by the provided amount.
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

        if (!taskExecutionService.isShutdown()) {
            this.taskExecutionService.submit(() -> {

                this.fireEvent(new GenericAudioEvent(
                        this,
                        GameAudio.RAT_DEATH.getResource()
                ));

                this.fireEvent(new EntityDeathEvent(
                        this,
                        null,
                        null
                ));
            });
            taskExecutionService.shutdown();
        }
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
     * Called by the loader object of the Entity for when it is first placing
     * the entity into the game. This informs the entity that the game is now
     * placing it into the origin point represented by the Entities
     * {@link #getRow()} and {@link #getCol()}.
     *
     * @param tile The origin TileData object that the entity now exists on.
     * @param map  The game map that the entity was placed on to.
     */
    @Override
    public void entityPlacedByLoader(final TileData tile,
                                     final ContextualMap map) {
        // Display rat image
        if (tile.getTile() instanceof Path) {
            this.fireEvent(new EntityLoadEvent(
                    this,
                    getDisplaySprite(),
                    0
            ));

            // Don't display any image
        } else {
            this.fireEvent(new EntityLoadEvent(
                    this,
                    null,
                    0
            ));
        }
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
