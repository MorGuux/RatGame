package game;

import game.contextmap.ContextualMap;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.rat.Rat;
import game.generator.RatItemGenerator;
import game.player.Player;
import game.tile.Tile;
import game.tile.grass.Grass;
import game.tile.grass.GrassSprite;

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
    private static final int UPDATE_TIME_FRAME = 1000;

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
        this.properties = properties;
        this.manager = manager;
        this.entityIterator = manager.getEntityIterator();

        // Initialise game states
        this.isPaused = new AtomicBoolean();
        this.isGameOver = new AtomicBoolean();
        this.hostileEntityCount = new AtomicInteger();

        // Allows concurrent queuing and de-queuing
        spawnQueue = new LinkedBlockingDeque<>();
    }

    /**
     * Starts the game; unpauses the game. By initialising the game update loop.
     *
     * @throws IllegalStateException If the game is over, {@link #isGameOver()}.
     */
    public void startGame() {
        // todo initialise game loop

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
            this.gameLoop.cancel();

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

        // todo some of these tasks can be extracted to their own methods.

        // Cancel game loop and return on game end (This would only ever get
        // executed once)
        if (properties.getMaxHostileEntities() <= hostileEntityCount.get()) {
            System.out.println("Maximum hostile entities detected");
            assert !this.isGameOver();
            this.isGameOver.set(true);
            this.gameLoop.cancel();
            return;
        }

        // Cancel game loop and return without doing anything
        if (isGamePaused()) {
            this.gameLoop.cancel();
            return;
        }

        // --------------------------------------
        // < - - - - ENTITY UPDATING - - - - > \\

        // Poll for entities to update (also releases outstanding references)
        if (!entityIterator.hasNext()) {
            manager.releaseIterator(entityIterator);

            // Spawn entities
            if (!spawnQueue.isEmpty()) {
                spawnQueue.forEach((e) -> {
                    System.out.printf("Spawning: [%s] [%s, %s]%n",
                            e,
                            e.getRow(),
                            e.getCol()
                    );
                    manager.addEntity(e);
                });
            }

            // https://youtu.be/QcbR1J_4ICg?t=57
            manager.getContextMap().collectDeadEntities();
            entityIterator = manager.getEntityIterator();

            // Reset count
            this.hostileEntityCount.set(0);
        }

        // Update a single entity
        if (entityIterator.hasNext()) {
            final Entity e = entityIterator.next();
            e.update(manager.getContextMap(), this);

            // Update count if the entity is hostile
            if (e.isHostile() && !e.isDead()) {
                hostileEntityCount.getAndIncrement();
            }

            // Remove from game if dead
            if (e.isDead()) {
                entityIterator.remove();
            }
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
                final Tile t = new Grass(GrassSprite.BARE, row, col);
                tiles[row][col] = t;
            }
        }

        Entity rat = new Rat(3, 3);

        ContextualMap map = new ContextualMap(tiles, 6, 6);
        RatGameManager m = new RatGameManager(new Entity[] {rat}, map);

        RatGameProperties properties = new RatGameProperties(
                (e) -> System.out.println("E"),
                new RatItemGenerator(),
                5,
                new Player("Jack")
        );

        final RatGame game = new RatGame(m, properties);
        System.out.println("Starting game with 1 rat existing.");
        System.out.println("Is game over?: " + game.isGameOver());
        game.startGame();

        System.out.println("Adding 4 rats to the game");
        game.spawnEntity(new Rat(0, 0));
        game.spawnEntity(new Rat(1, 1));
        game.spawnEntity(new Rat(1, 2));
        game.spawnEntity(new Rat(1, 2));

        System.out.println("Is game over?: " + game.isGameOver());

    }
}
