package game.generator;

import game.entity.Item;
import game.event.GameActionListener;
import game.event.impl.entity.specific.item.GeneratorUpdateEvent;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Item Generator wraps time and size constraint limitations to an Item
 * object. Allowing only a set number of instances to be created using the
 * instance before requiring a wait/timeout.
 *
 * @param <T> The item this generator will generate.
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class ItemGenerator<T extends Item> {

    // todo onUsageUpdate event, onItemUsedEvent

    /**
     * The item class this generator, generates.
     */
    private final Class<T> itemClass;

    /**
     * Item factory that creates the target item.
     */
    private final ItemFactory<T> itemFactory;

    /**
     * Time in milliseconds needed to wait minimum before gaining a usage.
     */
    private final int refreshTime;

    /**
     * Elapsed time in milliseconds should never be above the refresh time.
     */
    private final AtomicInteger variableTime;

    /**
     * Maximum number of usages ever allowed at any one time.
     */
    private final int maximumUsages;

    /**
     * Currently available usages.
     */
    private final AtomicInteger availableUsages;

    /**
     * Listener object that gets pinged on updates.
     */
    private GameActionListener listener;

    /**
     * Constructs the item generator from a pre-existing state set.
     *
     * @param itemClass     Target item class to generate.
     * @param itemFactory   Factory to create the item once provided args.
     * @param refreshTime   Time in milliseconds required to wait before giving
     *                      another usage.
     * @param curTime       Current time of the generator in milliseconds.
     * @param maximumUsages Maximum ever number of usages that the generator
     *                      will let exist at any one time.
     * @param curUsages     Current number of usages that the generator has
     *                      available for use.
     */
    public ItemGenerator(final Class<T> itemClass,
                         final ItemFactory<T> itemFactory,
                         final int refreshTime,
                         final int curTime,
                         final int curUsages,
                         final int maximumUsages) {
        deferNullity(itemClass, itemFactory);
        this.itemClass = itemClass;
        this.itemFactory = itemFactory;
        this.refreshTime = refreshTime;
        this.variableTime = new AtomicInteger(curTime);
        this.maximumUsages = maximumUsages;
        this.availableUsages = new AtomicInteger(curUsages);
    }

    /**
     * Add a listener to the Generator which gets sent targeted events for
     * item usage updates.
     *
     * @param listener The listener to be pinged on updates.
     */
    public void setListener(final GameActionListener listener) {
        this.listener = listener;
    }

    /**
     * Checks for nullity within the provided object set.
     *
     * @param objs Objects to test.
     * @throws NullPointerException If any of the objects are {@code null}.
     */
    private void deferNullity(final Object... objs) {
        Arrays.stream(objs).forEach(Objects::requireNonNull);
    }

    /**
     * Updates the variable time by the provided time frame. Which depending
     * on how much time has passed may provide more usages for this item
     * generator.
     *
     * @param timeFrame Time in milliseconds that has passed.
     */
    public synchronized void update(final int timeFrame) {
        int cur = this.variableTime.get();

        if ((cur + timeFrame) > refreshTime) {
            int possibleUsages = (cur + timeFrame) / refreshTime;
            this.variableTime.set(
                    (cur + timeFrame) - refreshTime * possibleUsages
            );
            this.addUsages(possibleUsages);

        } else {
            this.variableTime.set(cur + timeFrame);
        }

        this.listener.onAction(new GeneratorUpdateEvent(this));
    }

    /**
     * Adds the specified number of usages if it is possible to do so without
     * violating space constraints.
     *
     * @param usages The number of usages to add.
     */
    private void addUsages(final int usages) {
        this.availableUsages.set(
                Math.min((availableUsages.get() + usages), maximumUsages)
        );
    }

    /**
     * @param row The row that the item should be set at.
     * @param col The col that the item should be set at.
     * @return Newly constructed item.
     * @throws IllegalStateException If there are no usages available.
     */
    public synchronized T generateItem(final int row,
                                       final int col)
            throws IllegalStateException {
        if (availableUsages.get() > 0) {
            availableUsages.decrementAndGet();
            return itemFactory.create(row, col);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Get the class type of the item that this generator, generates.
     *
     * @return Class instance of the target type.
     */
    public Class<T> getItemClass() {
        return itemClass;
    }

    /**
     * @return Time in Milliseconds required to {@link #update(int)} by in
     * order to obtain another usage.
     */
    public int getRefreshTime() {
        return refreshTime;
    }

    /**
     * @return Current time in Milliseconds waited by the generator.
     */
    public int getVariableTime() {
        return variableTime.get();
    }

    /**
     * @return Maximum ever number of usages that the generator will allow at
     * any one time.
     */
    public int getMaximumUsages() {
        return maximumUsages;
    }

    /**
     * @return The current number of usages this generator has available.
     */
    public int getAvailableUsages() {
        return availableUsages.get();
    }

    /**
     * @return {@code true} if there is at least one available usage.
     * Otherwise, if not then {@code false} is returned.
     */
    public boolean hasAvailableUsages() {
        return availableUsages.get() > 0;
    }

    /**
     * @return Display sprite image for the target item.
     */
    public URL getDisplaySprite() {
        final Item i = itemFactory.create(0, 0);
        return i.getDisplaySprite();
    }

    /**
     * Compiles the current generator state to an args string that can be
     * written to a file. The string itself utilises the following format:
     * <ul>
     *     <li>[ItemClassName, [REFRESH_TIME, CURRENT_TIME, CURRENT_USAGES,
     *     MAX_USAGES]]</li>
     * </ul>
     *
     * @return Formatted string.
     */
    public synchronized String buildToString() {
        // Spaces are not needed but easier to read
        final String base = "[%s, [%s, %s, %s, %s]]";

        return String.format(base,
                itemClass.getSimpleName(),
                refreshTime,
                variableTime.get(),
                availableUsages.get(),
                maximumUsages
        );
    }
}
