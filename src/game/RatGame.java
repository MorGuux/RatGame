package game;

import game.entity.Entity;
import game.entity.Item;
import game.player.Player;

import java.util.ListIterator;
import java.util.Timer;

/**
 * The main game class. Contains the main game loop, functions to start and
 * pause the game, spawn entities, and a game update timer that is passed to
 * all entities to allow them to update themselves.
 * @author Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */
public class RatGame {

    private final RatGameProperties properties;
    private final RatGameManager manager;
    /**
     * Is the game paused?
     */
    private boolean isPaused;
    /**
     * Is the game over?
     */
    private boolean isGameOver;

    /**
     * The game update timer.
     */
    private Timer gameLoop;

    /**
     * Creates the game, using the manager and a properties reference.
     * @param manager The game manager.
     * @param properties The properties of the game.
     */
    public RatGame(RatGameManager manager, RatGameProperties properties) {
        this.properties = properties;
        this.manager = manager;
    }

    /**
     * Starts the game.
     */
    public void startGame() {

    }

    /**
     * Pauses the game.
     */
    public void pauseGame() {
        //TODO Pause the game, halt the update timer.
        this.isPaused = true;
    }

    /**
     * Use an item (place it on the map) from the inventory.
     */
    public void useItem(Class<Item> item, int row, int col) {

    }

    /**
     * Spawns an entity on the map.
     * @param entity The entity to spawn.
     */
    public void spawnEntity(Entity entity) {
        manager.addEntity(entity);
    }

    /*public Leaderboard getLeaderboard() {
        return null;
    }
     */

    /**
     * Gets the currently active player.
     * @return The currently active player.
     */
    public Player getPlayer() {
        return properties.getPlayer();
    }

    /**
     * Calls the update method on all entities, so they can react to the game,
     * other entities and move around.
     */
    private void gameUpdateLoop() {
        ListIterator<Entity> entityIterator = manager.getEntityIterator();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            entity.update(manager.getContextMap(), this);
        }

    }

    /**
     * Gets if the game is currently paused.
     * @return Is the game paused?
     */
    public boolean isGamePaused() {
        return isPaused;
    }

    /**
     * Gets if the game is over.
     * @return Is the game over?
     */
    public boolean isGameOver() {
        return isGameOver;
    }

}
