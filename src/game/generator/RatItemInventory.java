package game.generator;


import game.entity.Item;
import game.event.GameActionListener;

import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rat Item Generator acts as a single point of access for semi-random access
 * to a number of different {@link ItemGenerator}'s with the ability to apply
 * to all and ascertain type conversions.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class RatItemInventory {

    /**
     * Map of generators for semi-random access to each entry.
     */
    private final Map<Class<? extends Item>,
            ItemGenerator<? extends Item>> generators;


    /**
     * Construct an empty generator.
     */
    public RatItemInventory() {
        this.generators = new HashMap<>();
    }

    /**
     * Add the provided single item generator to the generator set so that it
     * can be accessed at a later date.
     * <p>
     * Note that if two generators of the same type are detected then the
     * latest generator overwrites the oldest.
     *
     * @param generator The generator to add.
     */
    public void addGenerator(final ItemGenerator<?> generator) {
        generators.put(generator.getItemClass(), generator);
    }

    /**
     * @param itemClass Class to check for existence of.
     * @return {@code true} if there exists a generator of the target class.
     * Otherwise, if not {@code false} is returned.
     */
    public boolean exists(final Class<? extends Item> itemClass) {
        // The <? extends Item> is kinda redundant/meaningless, but
        // associatively if not <? extends item> then false is guaranteed.
        // This rule applies to all, even Object.
        return generators.containsKey(itemClass);
    }

    /**
     * Checks if the provided generator has any available usages.
     *
     * @param itemClass The generator to check for.
     * @return {@code true} if the generator has at least one usage.
     * Otherwise, if the generator has no usages, or the generator doesn't
     * exist then {@code false} is returned.
     * @see ItemGenerator#hasAvailableUsages()
     */
    public boolean hasUsages(final Class<? extends Item> itemClass) {
        if (generators.containsKey(itemClass)) {
            return generators.get(itemClass).hasAvailableUsages();
        } else {
            return false;
        }
    }

    /**
     * @param itemClass Class item to get.
     * @param row   Row to generate at.
     * @param col   Column to generate at.
     * @param <T>   The type of the item to generate.
     * @return A newly constructed Item at the provided Row and Column.
     * @throws IllegalStateException        If no target generator exists for
     *                                      the provided Class type.
     * @throws MalformedParametersException If no usages are available for
     *                                      the target generator.
     */
    public <T extends Item> T get(final Class<T> itemClass,
                                  final int row,
                                  final int col) {
        if (generators.containsKey(itemClass)) {
            final ItemGenerator<?> gen = generators.get(itemClass);

            if (gen.hasAvailableUsages()) {
                final Item i = gen.generateItem(row, col);
                // This will always work as we use the class provided by the
                // ItemGenerator; which is <T> where T is the item type, thus
                // if the map contains the itemClass it's castable.
                return itemClass.cast(i);

                // If no usages available
            } else {
                throw new MalformedParametersException();
            }
            // If no target generator detected
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Get the underlying Generator for the provided class if one exists.
     *
     * @param itemClass Generator target class.
     * @return Underlying item generator for the target class.
     * @throws IllegalStateException If no generator exists for the target
     *                               class.
     * @see #exists(Class)
     */
    public ItemGenerator<? extends Item> getGenerator(
            final Class<? extends Item> itemClass) {

        if (generators.containsKey(itemClass)) {
            return generators.get(itemClass);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Get all item generators for all item classes
     *
     * @return All item generators.
     * @throws IllegalStateException If no generator exists for the target
     *                               class.
     * @see #exists(Class)
     */
    public List<ItemGenerator<? extends Item>> getGenerators() {
        return new ArrayList<>(generators.values());
    }

    /**
     * Update all the Generators to use the provided listener.
     *
     * @param listener The listener to attach to the generators.
     */
    public void setListener(final GameActionListener listener) {
        generators.forEach((c, g) -> g.setListener(listener));
    }

    /**
     * Update all generators by the provided time frame in milliseconds.
     *
     * @param timeFrame Time in milliseconds to update by.
     * @throws MalformedParametersException If time frame is a negative value.
     * @see ItemGenerator#update(int)
     */
    public void updateGenerators(final int timeFrame) {
        if (timeFrame > 0) {
            generators.forEach((c, g) -> g.update(timeFrame));
        } else {
            throw new MalformedParametersException();
        }
    }

    /**
     * Compiles all generators and their current state to a single string
     * where each entry is separated by a new line.
     *
     * @return Formatted string.
     * @see ItemGenerator#buildToString()
     */
    public String buildAllToString() {
        final StringBuilder sb = new StringBuilder();
        generators.forEach((c, g) -> sb.append(g.buildToString())
                .append(System.lineSeparator())
        );
        return sb.toString();
    }
}
