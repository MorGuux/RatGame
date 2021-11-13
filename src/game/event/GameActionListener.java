package game.event;

/**
 * Base game event listener which allows the handling of a Game Event.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface GameActionListener {
    void onAction(GameEvent event);
}
