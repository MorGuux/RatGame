package game;

import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.rat.Rat;
import game.event.impl.entity.specific.game.GameEndEvent;
import game.event.impl.entity.specific.game.GamePausedEvent;
import game.event.impl.entity.specific.game.GameStateUpdateEvent;
import game.generator.RatItemInventory;
import game.level.reader.RatGameSaveFile;
import game.level.reader.exception.RatGameFileException;
import game.level.savecontext.RatGameSaveContext;
import game.player.Player;
import game.player.leaderboard.Leaderboard;
import game.tile.base.path.Path;
import game.tile.exception.UnknownSpriteEnumeration;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The main game class. Contains the main game loop, functions to start and
 * pause the game, spawn entities, and a game update timer that is passed to
 * all entities to allow them to update themselves.
 *
 * @author Morgan Gardner
 * @version 0.4
 * Copyright: N/A
 */
public class RatGame {

    /**
     * The slowest speed the game update loop will run at.
     */
    public static final int GAME_SLOWEST_TIME_FRAME = 400;
    /**
     * The fastest speed the game update loop will run at.
     */
    public static final int GAME_FASTEST_TIME_FRAME = 100;
    /**
     * Determines how often the game updates. This is the default value for
     * the game.
     */
    private static final int BASE_UPDATE_TIME_FRAME = 275;

    /**
     * Game properties object that stores the game specific details. Things
     * such as the target listener adapter, and the player.
     */
    private final RatGameProperties properties;

    /**
     * Manager which manages the entities and the map they interact on.
     */
    private final RatGameManager manager;

    /**
     * Is the game paused?
     */
    private final AtomicBoolean isPaused;

    /**
     * Is the game over?
     */
    private final AtomicBoolean isGameOver;

    /**
     * If the game is over, has the player won or lost?
     */
    private final AtomicBoolean isGameWon;

    /**
     * The game update timer.
     */
    private Timer gameLoop;

    /**
     * The total number of entities that exist in the game.
     */
    private final AtomicInteger totalEntityCount;

    /**
     * The number of entities in the game that are registered as 'hostile'
     * through the {@link Entity#isHostile()} method.
     * <p>
     * This variable is both a game state, and game property. But I think the
     * state value is more important, so it's directly in the Game class.
     */
    private final AtomicInteger hostileEntityCount;

    /**
     * The count of the number of hostile male entities.
     */
    private final AtomicInteger hostileMaleEntityCount;

    /**
     * The count of the number of hostile female entities.
     */
    private final AtomicInteger hostileFemaleEntityCount;
    /**
     * The rate at which the game loop will update all entities in the game.
     */
    private final AtomicInteger updateTimeFrame;

    /**
     * Internal entity update 'queue'.
     */
    private ListIterator<Entity> entityIterator;

    /**
     * Queue of  entities that should be added to the game on any given update
     * tick.
     */
    private final LinkedBlockingDeque<Entity> spawnQueue;

    /**
     * Creates the game, using the manager and a properties reference.
     *
     * @param manager    The game manager.
     * @param properties The properties of the game.
     */
    public RatGame(final RatGameManager manager,
                   final RatGameProperties properties) {
        Objects.requireNonNull(manager);
        Objects.requireNonNull(properties);

        this.properties = properties;
        this.manager = manager;

        // Initialise game states
        this.isPaused = new AtomicBoolean();
        this.isGameOver = new AtomicBoolean();
        this.isGameWon = new AtomicBoolean();

        // Game update loop defaults
        this.updateTimeFrame = new AtomicInteger(BASE_UPDATE_TIME_FRAME);

        this.totalEntityCount = new AtomicInteger();
        this.hostileEntityCount = new AtomicInteger();
        this.hostileMaleEntityCount = new AtomicInteger();
        this.hostileFemaleEntityCount = new AtomicInteger();

        // This primarily is for side effects. The iterator must be empty
        // before going into the game loop for the first time. There is
        // probably a way around it but this doesn't really hurt much.
        this.entityIterator = manager.getEntityIterator();
        entityIterator.forEachRemaining(i -> {
            if (i.isHostile()) {
                hostileEntityCount.getAndIncrement();
                if (((Rat) i).getSex().equals(Rat.Sex.MALE)) {
                    hostileMaleEntityCount.getAndIncrement();
                } else {
                    hostileFemaleEntityCount.getAndIncrement();
                }
            }
            totalEntityCount.getAndIncrement();
        });

        // Allows concurrent queuing and de-queuing
        spawnQueue = new LinkedBlockingDeque<>();
    }

    /**
     * Starts the game; unpauses the game. By initialising the game update loop.
     *
     * @throws IllegalStateException If the game is over, {@link #isGameOver()}.
     */
    public void startGame() {
        if (!isGameOver()) {
            this.isPaused.set(false);

            // Whenever we escape the update loop, the previous timer was
            // voided.
            this.gameLoop = new Timer();
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    gameUpdateLoop();
                }
            };
            this.gameLoop.scheduleAtFixedRate(
                    task,
                    0,
                    updateTimeFrame.get()
            );

            // Game will not be started if it is finished.
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * @return The current update time frame for the game loop.
     */
    public int getUpdateTimeFrame() {
        return this.updateTimeFrame.get();
    }

    /**
     * Set the game update time frame to the provided value this affects how
     * fast the game is played.
     *
     * @param timeFrame The time between each update in milliseconds.
     * @throws IllegalStateException If the game is not paused.
     */
    public void setUpdateTimeFrame(final int timeFrame) {
        if (!isGamePaused()) {
            throw new IllegalStateException();
        }

        if (timeFrame < GAME_SLOWEST_TIME_FRAME
                && timeFrame > GAME_FASTEST_TIME_FRAME) {
            this.updateTimeFrame.set(timeFrame);
        }
    }

    /**
     * Resets the game update time frame back to the default value.
     *
     * @throws IllegalStateException If the game is not paused.
     */
    public void resetTimeFrame() {
        if (!isGamePaused()) {
            throw new IllegalStateException();
        }

        this.updateTimeFrame.set(BASE_UPDATE_TIME_FRAME);
    }

    /**
     * Pauses the game if the game is not paused and not over.
     *
     * @throws IllegalStateException If the game is currently paused, or if
     *                               the game has finished.
     */
    public void pauseGame() {
        if (!isGamePaused() && !isGameOver()) {
            this.isPaused.set(true);
            this.gameLoop.cancel();

            // Game paused event
            this.properties.getActionListener().onAction(new GamePausedEvent(
                    this
            ));

        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Places the item into the game at the provided row, col if there are
     * available usages and that the target tile is a valid tile.
     *
     * @param item The item to spawn.
     * @param row  The row to spawn at.
     * @param col  The column to spawn at.
     * @return {@code true} if the item has been queued to be spawned.
     * Otherwise, if not then {@code false} is returned.
     */
    public boolean useItem(final Class<? extends Item> item,
                           final int row,
                           final int col) {

        // Should bind the 'can be placed on tile' thing to the generator
        // target item so that the tile to place is independent.

        final RatItemInventory inv
                = this.properties.getItemGenerator();

        final ContextualMap gameMap = this.manager.getContextMap();
        final TileData tile = gameMap.getTileDataAt(row, col);

        // Place only on tiles, when game not paused and game is not over.
        if ((tile.getTile() instanceof Path)
                && (!this.isGamePaused()
                && !this.isGameOver())) {

            // If usages available place item
            if (inv.exists(item) && inv.hasUsages(item)) {
                this.spawnEntity(inv.get(item, row, col));
                return true;

            } else {
                throw new IllegalStateException();
            }

            // Don't place item
        } else {
            return false;
        }
    }

    /**
     * Spawns an entity on the map.
     *
     * @param entity The entity to spawn.
     * @throws NullPointerException If entity is null.
     */
    public void spawnEntity(final Entity entity) {
        Objects.requireNonNull(entity);
        spawnQueue.add(entity);
    }

    /**
     * @return The leaderboard for the players on this level.
     */
    public Leaderboard getLeaderboard() {
        return this.properties.getLeaderboard();
    }

    /**
     * Gets the currently active player.
     *
     * @return The currently active player.
     */
    public Player getPlayer() {
        return properties.getPlayer();
    }

    /**
     * Updates all game entities in the game whilst also checking and
     * updating the current game state accordingly.
     * <p>
     * Tasks executed (In order):
     * <ol>
     *     <li>Checks if more entities exist</li>
     *     <li>Spawns any entities into the game that should be spawned</li>
     *     <li>Checks to see if the player has won the game</li>
     *     <li>Checks to see if the player has lost the game</li>
     *     <li>Checks to see if the game is paused</li>
     *     <li>Queues async execution of all entities in the game</li>
     *     <li>Updates the game state</li>
     * </ol>
     */
    private void gameUpdateLoop() {

        // Condition serves two purposes; Refresh the entity iterator when
        // empty and spawn entities whenever the queue has stuff.
        final boolean hasMoreEntities = entityIterator.hasNext();
        final boolean managerHasMoreEntities = manager.getSize() > 0;
        final boolean spawnQueueHasMoreEntities = !spawnQueue.isEmpty();

        if (!hasMoreEntities
                && (managerHasMoreEntities || spawnQueueHasMoreEntities)) {
            getEntitiesForUpdate();
        }

        // Is game won?
        if (hostileEntityCount.get() == 0) {
            isGameOver.set(true);
            this.isGameWon.set(true);
            gameLoop.cancel();

            // Award bonus points if any
            this.getPlayer().setCurrentScore(
                    this.getPlayer().getCurrentScore() + getBonusPoints()
            );
            this.alertOfGameState();

            // Inform of game end
            this.properties.getActionListener().onAction(new GameEndEvent(
                    this,
                    this.properties
            ));

            return;
        }

        // Is game Over?
        if (properties.getMaxHostileEntities() <= hostileEntityCount.get()) {
            assert !this.isGameOver();
            this.isGameOver.set(true);
            this.gameLoop.cancel();

            // Game end event
            this.properties.getActionListener().onAction(new GameEndEvent(
                    this,
                    this.properties
            ));

            return;
        }

        // Is game paused?
        if (isGamePaused()) {
            this.gameLoop.cancel();

            return;
        }


        // Update all entities while not paused
        while (entityIterator.hasNext() && !this.isGamePaused()) {
            final Entity e = entityIterator.next();
            this.updateSingleEntity(e);
        }

        // Update how long the user has been playing
        this.properties.getPlayer().setPlayTime(
                this.getPlayer().getPlayTime() + this.updateTimeFrame.get()
        );

        // Update game state
        this.alertOfGameState();
        this.properties.getItemGenerator().updateGenerators(
                this.updateTimeFrame.get()
        );
    }

    /**
     * Calculates the number of bonus points that the player should be
     * awarded from the game completion.
     *
     * @return The number of bonus points to award.
     */
    private int getBonusPoints() {
        final int expectedTime = this.properties.getExpectedClearTime();
        final int clearTime = expectedTime - this.getPlayer().getPlayTime();

        final int minClearTime = 1000;
        if (clearTime >= minClearTime) {

            // 1 point per second left
            return clearTime / minClearTime;
        } else {
            return 0;
        }
    }

    /**
     * Releases the Entity update 'queue' so that it can be re-queued with
     * more entities that need to be updated. This also spawns any
     * outstanding entities in the Entity spawn queue.
     */
    private void getEntitiesForUpdate() {
        manager.releaseIterator(this.entityIterator);

        // Spawn entities
        if (!spawnQueue.isEmpty()) {
            spawnEntities();
        }

        // https://youtu.be/QcbR1J_4ICg?t=57
        manager.getContextMap().collectDeadEntities();
        entityIterator = manager.getEntityIterator();
    }

    /**
     * Spawns all entities in the entity spawn queue.
     */
    private void spawnEntities() {
        while (!spawnQueue.isEmpty()) {
            final Entity e = spawnQueue.remove();
            manager.addEntity(e);
            e.setListener(this.properties.getActionListener());

            e.entityPlacedByLoader(
                    manager.getContextMap().getOriginTile(e),
                    manager.getContextMap()
            );

            // Tally hostile entities
            if (e.isHostile()) {
                hostileEntityCount.getAndIncrement();
                if (((Rat) e).getSex().equals(Rat.Sex.MALE)) {
                    hostileMaleEntityCount.getAndIncrement();
                } else {
                    hostileFemaleEntityCount.getAndIncrement();
                }
            }
            totalEntityCount.getAndIncrement();
        }
        System.out.println();
    }

    /**
     * Controls whether the current entity should be updated or removed from
     * the game.
     *
     * @param e The entity to update.
     */
    private void updateSingleEntity(final Entity e) {

        // Dead entities are not updated
        if (e.isDead()) {
            entityIterator.remove();

            totalEntityCount.getAndDecrement();

            // Deduct hostile entities
            if (e.isHostile()) {
                hostileEntityCount.getAndDecrement();
                if (((Rat) e).getSex().equals(Rat.Sex.MALE)) {
                    hostileMaleEntityCount.getAndDecrement();
                } else {
                    hostileFemaleEntityCount.getAndDecrement();
                }

                final int curPoints = this.getPlayer().getCurrentScore();
                this.getPlayer().setCurrentScore(
                        curPoints + e.getDeathPoints()
                );
            }

        } else {
            e.update(manager.getContextMap(), this);
        }
    }

    /**
     * Fires of an event to the listener informing of the new game state in
     * time.
     */
    private void alertOfGameState() {
        final RatGameProperties prop = this.properties;
        this.properties.getActionListener().onAction(new GameStateUpdateEvent(
                this,
                this.hostileEntityCount.get(),
                this.hostileMaleEntityCount.get(),
                this.hostileFemaleEntityCount.get(),
                prop.getExpectedClearTime() - prop.getPlayer().getPlayTime()
        ));
    }

    /**
     * @return Is the game paused?
     */
    public boolean isGamePaused() {
        return isPaused.get();
    }

    /**
     * @return Is the game over?
     */
    public boolean isGameOver() {
        return isGameOver.get();
    }

    /**
     * @return {@code true} if the player has won the game.
     */
    public boolean isGameWon() {
        return isGameWon.get();
    }

    /**
     * @return The total number of entities that exist in the game.
     */
    public int getTotalNumberOfEntities() {
        return totalEntityCount.get();
    }

    /**
     * Saves the current state of the game into any possible free slot that
     * can be saved into.
     *
     * @throws UnknownSpriteEnumeration If the file used to load this game
     *                                  has become malformed.
     * @throws RatGameFileException     If the rat game file used to load this
     *                                  has been modified.
     * @throws IOException              If any occur whilst trying to read,
     *                                  write, and create files.
     */
    public void saveGame()
            throws UnknownSpriteEnumeration,
            RatGameFileException,
            IOException {
        if (this.isGamePaused() && !this.isGameOver()) {

            // This is null if not loaded from a save file
            final RatGameSaveFile existingSave
                    = this.properties.getSavePoint();

            // Save to existing file
            final RatGameSaveContext context;
            if (existingSave != null) {
                context = new RatGameSaveContext(
                        existingSave, properties.getPlayer()
                );

                // Create new file to save on
            } else {
                context = new RatGameSaveContext(
                        this.properties.getPlayer()
                );

            }
            context.saveGameInfo(
                    this.properties.getItemGenerator(),
                    compileEntities()
            );

            // You are not allowed to save whilst the game is running
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Event that is fired when a rat changes sex. Used to update the ratio
     * bar that indicates how many of each sex of rat are alive.
     * @param prevSex Previous sex of rat.
     * @param target Rat that had its sex changed.
     */
    public void stateEntityUpdated(final Rat.Sex prevSex,
                                   final Rat target) {

        // Rat is now a female
        if (prevSex.equals(Rat.Sex.MALE)
                && target.getSex().equals(Rat.Sex.FEMALE)) {
            this.hostileMaleEntityCount.getAndDecrement();
            this.hostileFemaleEntityCount.getAndIncrement();
        }

        // Rat is now a male
        if (prevSex.equals(Rat.Sex.FEMALE)
                && target.getSex().equals(Rat.Sex.MALE)) {
            this.hostileFemaleEntityCount.getAndDecrement();
            this.hostileMaleEntityCount.getAndIncrement();
        }
    }

    /**
     * Compiles all entities into a Single string using the
     * {@link Entity#buildToString(ContextualMap)} method.
     *
     * @return Built entities.
     */
    private String compileEntities() {
        final StringJoiner joiner
                = new StringJoiner(System.lineSeparator());
        this.manager.releaseIterator(this.entityIterator);
        this.entityIterator = this.manager.getEntityIterator();

        // Build the entities
        while (entityIterator.hasNext()) {
            joiner.add("    " + entityIterator.next().buildToString(
                    this.manager.getContextMap()
            ));
        }

        // Reset the iterator
        this.manager.releaseIterator(this.entityIterator);
        this.entityIterator = this.manager.getEntityIterator();
        return joiner.toString();
    }
}
