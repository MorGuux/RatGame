package game.event.impl.entity.specific.load;

import game.event.impl.entity.specific.item.GeneratorUpdateEvent;
import game.generator.ItemGenerator;

/**
 * Wraps when a generator needs to be loaded into the game.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class GeneratorLoadEvent extends GeneratorUpdateEvent {

    // This is a carbon copy of the generator update event; just with a more
    // specific name.

    /**
     * Constructs a game event from a target author.
     *
     * @param author The author of the event.
     * @throws NullPointerException If the author is a {@code null}.
     */
    public GeneratorLoadEvent(final ItemGenerator<?> author) {
        super(author);
    }
}
