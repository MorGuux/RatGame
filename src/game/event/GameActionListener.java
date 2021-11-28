package game.event;

/**
 * Base game event listener which allows the handling of a Game Event.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface GameActionListener {

    /**
     * On some {@link GameEvent} handle the result from said event. The
     * events are not limited to any singular Type and type conversion will
     * need to be done.
     *
     * @param event Event data of the event/action that occurred.
     */
    void onAction(GameEvent<?> event);
}
