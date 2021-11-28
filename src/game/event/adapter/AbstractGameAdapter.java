package game.event.adapter;

import game.event.GameActionListener;
import game.event.GameEvent;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 * Game adapter wraps all known {@link game.event.GameEvent} and provides a
 * set of abstract methods that can be implemented to utilise the events.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public abstract class AbstractGameAdapter implements GameActionListener {

    //todo comment

    /**
     * Exception for when the target method invocation fails due to some
     * reason.
     */
    private static final String ERR_INVOKE_ERROR = "Error invocation of the "
            + "event: \"%s\" failed with the target method: \"%s\" with the "
            + "exception method:%n\"%s\"";

    /**
     * Event and Handler map to give semi-random access to targeted events.
     */
    private final HashMap<Class<?>, Method> eventHandlerMap;

    /**
     * Constructs a default Game adapter that wraps all game events.
     */
    public AbstractGameAdapter() {
        // Hash map for semi random access
        this.eventHandlerMap = new HashMap<>();

        final Method[] declared = getClass().getDeclaredMethods();
        final Class<?> voidReturn = void.class;
        final Class<?> eventConsumer = GameEvent.class;

        for (Method m : declared) {
            final Parameter[] params = m.getParameters();
            // Ensure method is 1 param, void, and the only parameter
            // inherits from GameEvent
            if (params.length == 1
                    && eventConsumer.isAssignableFrom(params[0].getType())
                    && voidReturn.isAssignableFrom(m.getReturnType())) {

                this.eventHandlerMap.put(params[0].getType(), m);
            }
        }
    }

    /**
     * Delegates an event to its handler.
     *
     * @param event The event to delegate.
     */
    @Override
    public void onAction(final GameEvent<?> event) {
        if (eventHandlerMap.containsKey(event.getClass())) {
            try {
                eventHandlerMap.get(event.getClass()).invoke(this, event);

                // Invocation failure
            } catch (InvocationTargetException | IllegalAccessException e) {
                this.onEventInvokeException(event, e,
                        eventHandlerMap.get(event.getClass())
                );
            }
            // Event unknown
        } else {
            this.onUnknownEvent(event);
        }
    }

    /**
     * Case when a method invocation fails.
     *
     * @param event     The event that failed.
     * @param exception The exception that was thrown.
     * @param target    The target method which produced the exception.
     */
    protected void onEventInvokeException(final GameEvent<?> event,
                                          final Exception exception,
                                          final Method target) {
        throw new IllegalStateException(String.format(
                ERR_INVOKE_ERROR,
                event.getClass().getSimpleName(),
                target.getName(),
                exception.getMessage()
        ));
    }

    /**
     * @param event The unknown event.
     */
    protected void onUnknownEvent(final GameEvent<?> event) {
        System.err.println(
                "Unknown Event: " + event.getClass().getSimpleName()
        );
    }

    /**
     * Game paused event.
     * @param e Pause event.
     */
    protected abstract void onGamePaused(GamePausedEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onGameEndEvent(GameEndEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onGameLoadEvent(GameLoadEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onEntityLoadEvent(EntityLoadEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onGeneratorLoadEvent(GeneratorLoadEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onScoreUpdate(ScoreUpdateEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onEntityMovedEvent(EntityMovedEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onEntityOccupyTileEvent(EntityOccupyTileEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onEntityDeathEvent(EntityDeathEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onSpriteChangeEvent(SpriteChangeEvent e);

    /**
     *
     * @param e
     */
    protected abstract void onGeneratorUpdate(GeneratorUpdateEvent e);
}
