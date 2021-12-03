package game;

import game.contextmap.ContextualMap;
import game.entity.Entity;
import gui.game.dependant.tilemap.Coordinates;

import java.lang.reflect.MalformedParametersException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Rat Game Manager stores the games entities that are to be updated and the
 * map of which they reside on. Since this is designed to work around Threaded
 * access lots of the methods are a Hand out, Hand in work flow. In
 * that you request something, do something, then hand it back.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class RatGameManager {

    /**
     * Entity list that is never given out under any circumstances.
     */
    private final List<Entity> entityList;

    /**
     * Iterator which is passed around so that they can work with the
     * underlying data.
     */
    private ListIterator<Entity> iterator;

    /**
     * States whether the iterator has been handed out to someone or not.
     */
    private final AtomicBoolean isActiveIterator;

    /**
     * Game map that all entities reside upon. A place where they can all
     * interact with each other.
     */
    private final ContextualMap contextMap;

    /**
     * Constructs the Game manager from the Entity position map, where an
     * Entity is placed into the game at its X,Y value then at the positions
     * represented in the {@link Coordinates} Object.
     *
     * @param entities The entities that should exist in the game.
     * @param map      Map fully populated with the above entities.
     */
    public RatGameManager(final Entity[] entities,
                          final ContextualMap map) {
        entityList = new ArrayList<>();
        entityList.addAll(Arrays.asList(entities));
        isActiveIterator = new AtomicBoolean();

        // The entities should already be added to the map
        contextMap = map;
    }

    /**
     * Gets an iterator attached to the set of entities that this manager
     * manages.
     * <p>
     * The iterator provided is a shared iterator in that all calls use the
     * same one. Once you're finished with the iterator you must hand it in via
     * {@link #releaseIterator(ListIterator)} otherwise all calls to modify
     * will result in a {@link ConcurrentModificationException}.
     * <p>
     * All calls to this method should be checked with
     * {@link #isActiveIterator} and {@link #getSize()} otherwise exceptions
     * are unavoidable.
     *
     * @return Iterator of possibly no entities.
     * @throws ConcurrentModificationException If there exists an outstanding
     *                                         iterator iterating through the
     *                                         entities currently.
     * @throws IllegalStateException           If the number of Entities is Zero
     *                                         or less.
     */
    public synchronized ListIterator<Entity> getEntityIterator() {
        if (isActiveIterator.get()) {
            throw new ConcurrentModificationException();
        }

        if (getSize() > 0) {
            isActiveIterator.set(true);
            this.iterator = entityList.listIterator();
            return this.iterator;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Hand in the iterator you were provided by {@link #getEntityIterator()}
     * so that it can be properly disposed of and reset.
     *
     * @param entityListIterator The iterator to dispose of.
     * @throws MalformedParametersException If the provided iterator is
     *                                      one not obtained from
     *                                      {@link #getEntityIterator()}.
     * @implNote Technically don't need the parameter. But it ensures that
     * the one cancelling is the one that owns it.
     */
    public void releaseIterator(final ListIterator<Entity> entityListIterator) {
        if (entityListIterator.equals(iterator)) {
            // Flush the iterator completely
            while (entityListIterator.hasNext()) {
                entityListIterator.next();
            }

            isActiveIterator.set(false);
        } else {
            throw new MalformedParametersException();
        }
    }

    /**
     * @return The number of entities currently existing in the manager.
     * Regardless of if the entities are dead or alive.
     */
    public int getSize() {
        return entityList.size();
    }

    /**
     * Adds the provided entity to the game so that it can be interacted with
     * and updated.
     *
     * @param e The entity to add to the game.
     * @throws ConcurrentModificationException If there is an active iterator
     *                                         iterating through the entity
     *                                         list.
     */
    public void addEntity(final Entity e) {
        if (!isActiveIterator.get()) {
            entityList.add(e);
            this.contextMap.placeIntoGame(e);

        } else {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Get the contextual map for the game. This will be used by entities to
     * interact with other entities in the level.
     *
     * @return The contextual map for the game.
     */
    public ContextualMap getContextMap() {
        return contextMap;
    }

    /**
     * @return {@code true} if there is an alive iterator that is iterating
     * through the Entity list currently. Otherwise, if not then {@code false}
     * is returned.
     */
    public boolean hasActiveIterator() {
        return isActiveIterator.get();
    }
}
