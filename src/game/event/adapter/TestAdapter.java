package game.event.adapter;

import game.event.impl.entity.specific.game.GameEndEvent;
import game.event.impl.entity.specific.game.GamePausedEvent;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityMovedEvent;
import game.event.impl.entity.specific.general.EntityOccupyTileEvent;
import game.event.impl.entity.specific.general.SpriteChangeEvent;
import game.event.impl.entity.specific.item.GeneratorUpdateEvent;
import game.event.impl.entity.specific.load.EntityLoadEvent;
import game.event.impl.entity.specific.load.GameLoadEvent;
import game.event.impl.entity.specific.load.GeneratorLoadEvent;
import game.event.impl.entity.specific.player.ScoreUpdateEvent;

public class TestAdapter extends AbstractGameAdapter {

    /**
     * Game paused event.
     *
     * @param e Pause event.
     */
    @Override
    protected void onGamePaused(GamePausedEvent e) {
        System.out.println("Game Paused!");
    }

    /**
     * @param e
     */
    @Override
    protected void onGameEndEvent(GameEndEvent e) {
        System.out.println("Game Ended!");
    }

    /**
     * @param e
     */
    @Override
    protected void onGameLoadEvent(GameLoadEvent e) {
        System.out.println("Game Loaded!");
    }

    /**
     * @param e
     */
    @Override
    protected void onEntityLoadEvent(EntityLoadEvent e) {
        System.out.println("Entity Loaded!");
    }

    /**
     * @param e
     */
    @Override
    protected void onGeneratorLoadEvent(GeneratorLoadEvent e) {
        System.out.println("Generator Loaded!");
    }

    /**
     * @param e
     */
    @Override
    protected void onScoreUpdate(ScoreUpdateEvent e) {
        System.out.println("Score updated!");
    }

    /**
     * @param e
     */
    @Override
    protected void onEntityMovedEvent(EntityMovedEvent e) {
        System.out.println("Entity Moved!");
    }

    /**
     * @param e
     */
    @Override
    protected void onEntityOccupyTileEvent(EntityOccupyTileEvent e) {
        System.out.println("Entity Occupied Tile!");
    }

    /**
     * @param e
     */
    @Override
    protected void onEntityDeathEvent(EntityDeathEvent e) {
        System.out.println("Entity died!");
    }

    /**
     * @param e
     */
    @Override
    protected void onSpriteChangeEvent(SpriteChangeEvent e) {
        System.out.println("Entity Changed Sprite!");
    }

    /**
     * @param e
     */
    @Override
    protected void onGeneratorUpdate(GeneratorUpdateEvent e) {
        System.out.println("Generator Updated!");
    }

    public static void main(String[] args) {
        // THIS WILL ALWAYS THROW A NULL POINTER; AS THE AUTHOR FOR ALL
        // EVENTS CAN NOT BE NULL. Comment out the line: Objects
        // .requireNonNull in GameEvent<T> in order to run this
        TestAdapter adapter = new TestAdapter();

        adapter.onAction(new GamePausedEvent(null));
        adapter.onAction(new GameEndEvent(null));
        adapter.onAction(new GameLoadEvent(null, null));
        adapter.onAction(new EntityLoadEvent(null, null, 0));
        adapter.onAction(new GeneratorLoadEvent(null));
        adapter.onAction(new ScoreUpdateEvent(null, 0, null));
        adapter.onAction(new EntityMovedEvent(null, 0, 0, 0));
        adapter.onAction(new EntityOccupyTileEvent(null, 0, 0, 0, null, null));
        adapter.onAction(new EntityDeathEvent(null, null, null));
        adapter.onAction(new SpriteChangeEvent(null, 0, null));
        adapter.onAction(new GeneratorUpdateEvent(null));
    }
}
