package game.event.impl.entity;

import game.entity.Item;

import java.net.URL;

/**
 * Event base wraps a generic Item event that has not context or base just
 * wraps the underlying data.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public abstract class ItemEvent extends EntityEvent {

    /**
     * Constructs an entity event from the target entity.
     *
     * @param author The target entity.
     */
    public ItemEvent(final Item author) {
        super(author);
    }

    /**
     * @return Item cast event author.
     */
    private Item getAuthor() {
        return (Item) this.getEventAuthor();
    }

    /**
     * @return The static display sprite for this Item.
     */
    public URL getDisplaySprite() {
        return getAuthor().getDisplaySprite();
    }
}
