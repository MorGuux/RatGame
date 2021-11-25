package game;

import game.contextmap.ContextualMap;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.gas.Gas;
import game.entity.subclass.rat.Rat;
import game.generator.RatItemGenerator;
import game.player.Player;
import game.tile.Tile;
import game.tile.base.grass.Grass;
import game.tile.base.grass.GrassSprite;

import java.util.ListIterator;
import java.util.Objects;
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
 * @version 0.1
 * Copyright: N/A
 */
public class RatGame {

    /**
     * Determines how often every each entity is updated.
     */
    private static final int UPDATE_TIME_FRAME = 500;

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
     * The game update timer.
     */
    private Timer gameLoop;

    /**
     * The number of entities in the game that are registered as 'hostile'
     * through the {@link Entity#isHostile()} method.
     * <p>
     * This variable is both a game state, and game property. But I think the
     * state value is more important, so it's directly in the Game class.
     */
    private final AtomicInteger hostileEntityCount;

    /**
     * Internal entity update 'queue'
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
        if (manager.getSize() <= 0) {
            throw new IllegalStateException();
        }

        this.properties = properties;
        this.manager = manager;

        // Initialise game states
        this.isPaused = new AtomicBoolean();
        this.isGameOver = new AtomicBoolean();

        this.hostileEntityCount = new AtomicInteger();

        // This primarily is for side effects. The iterator must be empty
        // before going into the game loop for the first time. There is
        // probably a way around it but this doesn't really hurt much.
        this.entityIterator = manager.getEntityIterator();
        entityIterator.forEachRemaining(i -> {
            if (i.isHostile()) {
                hostileEntityCount.getAndIncrement();
            }
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
            this.gameLoop.scheduleAtFixedRate(task, 0, UPDATE_TIME_FRAME);

            // Game will not be started if it is finished.
        } else {
            throw new IllegalStateException();
        }
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
            System.out.printf("[PauseState: [ExistingEntities: %s], "
                            + "[SpawnQueue: %s],"
                            + " [HostileEntityCount: %s]]%n%n",
                    manager.getSize(),
                    spawnQueue.size(),
                    hostileEntityCount.get()
            );

        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Use an item (place it on the map) from the inventory.
     */
    public void useItem(final Class<Item> item,
                        final int row,
                        final int col) {
        // todo Check if item available, queue spawn entity.
    }

    /**
     * Spawns an entity on the map.
     *
     * @param entity The entity to spawn.
     * @throws NullPointerException If entity is null.
     */
    public synchronized void spawnEntity(final Entity entity) {
        Objects.requireNonNull(entity);
        spawnQueue.add(entity);
    }

    /*public Leaderboard getLeaderboard() {
        return null;
    }
     */

    /**
     * Gets the currently active player.
     *
     * @return The currently active player.
     */
    public Player getPlayer() {
        return properties.getPlayer();
    }

    /**
     * Spawns up to 'k' items, and updates a single entity. Returns
     * immediately if the game is paused or if the game is over.
     * <p>
     * This is kind of a recursive function just without the recursion :)
     * <p>
     * This method should do the following, in order:
     * <ol>
     *     <li>Check if hostile entity count >= max allowed</li>
     *     <li>Check if game is paused</li>
     *     <li>Spawn any entities/items if needed</li>
     *     <li>Update a single entity</li>
     * </ol>
     */
    private void gameUpdateLoop() {
        System.out.print("[UPDATE]\t\t\t");

        // Condition serves two purposes; Refresh the entity iterator when
        // empty and spawn entities whenever the queue has stuff.
        final boolean hasMoreEntities = entityIterator.hasNext();
        final boolean managerHasMoreEntities = manager.getSize() > 0;
        final boolean spawnQueueHasMoreEntities = !spawnQueue.isEmpty();

        if (!hasMoreEntities
                && (managerHasMoreEntities || spawnQueueHasMoreEntities)) {
            System.out.println("[Getting Entities]");
            getEntitiesForUpdate();
        }

        // Is game won?
        if (hostileEntityCount.get() == 0) {
            isGameOver.set(true);
            gameLoop.cancel();
            System.out.println("[Game won!]");
        }

        // Is game Over?
        if (properties.getMaxHostileEntities() <= hostileEntityCount.get()) {
            System.out.println("[Maximum hostile entities detected]");
            assert !this.isGameOver();
            this.isGameOver.set(true);
            this.gameLoop.cancel();
            return;
        }

        // Is game paused?
        if (isGamePaused()) {
            this.gameLoop.cancel();
            System.out.println("[Game paused]\n");
            return;
        }

        // Update a single entity
        if (entityIterator.hasNext()) {
            updateSingleEntity();
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
            System.out.printf(
                    "[Spawned Entity] - [%s, [%s,%s]] ",
                    e,
                    e.getRow(),
                    e.getCol()
            );

            // Tally hostile entities
            if (e.isHostile()) {
                hostileEntityCount.getAndIncrement();
            }
        }
        System.out.println();
    }

    /**
     * Controls whether the current entity should be updated or removed from
     * the game.
     */
    private void updateSingleEntity() {
        final Entity e = entityIterator.next();

        // Dead entities are not updated
        if (e.isDead()) {
            entityIterator.remove();

            // Deduct hostile entities
            if (e.isHostile()) {
                hostileEntityCount.getAndDecrement();
            }

            System.out.printf("[Entity %s is dead [%s]]%n", e,
                    hostileEntityCount.get());

        } else {
            e.update(manager.getContextMap(), this);
        }
    }

    /**
     * Gets if the game is currently paused.
     *
     * @return Is the game paused?
     */
    public boolean isGamePaused() {
        return isPaused.get();
    }

    /**
     * Gets if the game is over.
     *
     * @return Is the game over?
     */
    public boolean isGameOver() {
        return isGameOver.get();
    }

    // <---------------------TEST CODE---------------------->
    public static void main(String[] args) throws InterruptedException {
        final Tile[][] tiles = new Tile[6][6];

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                final Tile t = new Grass(GrassSprite.BARE_GRASS, row, col);
                tiles[row][col] = t;
            }
        }

        Rat rat = new Rat(3, 3);

        ContextualMap map = new ContextualMap(tiles, 6, 6);
        RatGameManager m = new RatGameManager(new Entity[]{rat}, map);

        RatGameProperties properties = new RatGameProperties(
                (e) -> System.out.println("E"),
                new RatItemGenerator(),
                5,
                new Player("Jack")
        );

        final RatGame game = new RatGame(m, properties);

        final Rat r0 = new Rat(0, 0);
        final Rat r1 = new Rat(0, 0);
        final Rat r2 = new Rat(0, 0);
        final Rat r3 = new Rat(0, 0);

        game.startGame();

        Thread.sleep(1000);

        game.spawnEntity(r0);
        game.spawnEntity(r1);

        game.pauseGame();
        Thread.sleep(2000);
        r0.kill();

        game.startGame();
        r1.kill();
        Thread.sleep(1000);

        rat.kill();
        game.pauseGame();
        Thread.sleep(2000);

        game.spawnEntity(r2);
        game.spawnEntity(r3);
        game.startGame();
        Thread.sleep(1000);

        r2.kill();
        Thread.sleep(1000);
        r3.kill();

        while (!game.isGameOver());
        System.out.println("Is game over?: " + game.isGameOver());

    }
}
