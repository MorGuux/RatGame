package game;

import game.entity.Entity;
import game.entity.Item;
import game.player.Player;

import java.util.Timer;

public class RatGame {

    private boolean isPaused;
    private boolean isGameOver;

    private Timer gameLoop;

    public RatGame(RatGameManager manager, RatGameProperties properties) {

    }

    public void startGame() {

    }

    public void pauseGame() {

    }

    public void useItem(Item item, int x, int y) {

    }

    public void spawnEntity(Entity entity) {

    }

    /*public Leaderboard getLeaderboard() {
        return null;
    }
     */

    public Player getPlayer() {
        return null;
    }

    private void gameUpdateLoop() {

    }

    public boolean isGamePaused() {
        return isPaused;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

}
