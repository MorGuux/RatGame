package game.event.impl.entity.specific.item;

import game.entity.Item;
import game.event.GameEvent;
import game.generator.ItemGenerator;

import java.net.URL;

/**
 * Wraps an ItemGenerator update event where any change could have occurred
 * to the generator.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class GeneratorUpdateEvent extends GameEvent<ItemGenerator<?>> {

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author The target entity.
     */
    public GeneratorUpdateEvent(final ItemGenerator<?> author) {
        super(author);
    }

    /**
     * @return Current number of usages the generator has.
     */
    public int getCurUsages() {
        return getEventAuthor().getAvailableUsages();
    }

    /**
     * @return The maximum number of usages the generator has.
     */
    public int getMaxUsages() {
        return getEventAuthor().getMaximumUsages();
    }

    /**
     * @return The total refresh time the generator has.
     */
    public int getRefreshTime() {
        return getEventAuthor().getRefreshTime();
    }

    /**
     * @return The current refresh time the generator has.
     */
    public int getCurRefreshTime() {
        return getEventAuthor().getVariableTime();
    }

    /**
     * @return The target class of the item generator.
     */
    public Class<? extends Item> getTargetClass() {
        return getEventAuthor().getItemClass();
    }

    /**
     * @return The resource for the display sprite for the target item of the
     * generator.
     */
    public URL getDisplaySprite() {
        return getEventAuthor().getDisplaySprite();
    }
}
