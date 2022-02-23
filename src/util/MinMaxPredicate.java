package util;

import java.util.function.Predicate;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class MinMaxPredicate implements Predicate<String> {

    /**
     * Min value non inclusive.
     */
    private final int min;

    /**
     * max value non inclusive.
     */
    private final int max;

    /**
     * Positive integer predicate instance allowing values 1 through to MAX - 1.
     */
    public static final MinMaxPredicate INT_SCOPED_PREDICATE
            = new MinMaxPredicate(
                    0,
            Integer.MAX_VALUE - 1
    );

    /**
     * Constructs the predicate from the given args.
     *
     * @param min The min value, non-inclusive.
     * @param max The max value non-inclusive.
     */
    public MinMaxPredicate(final int min,
                           final int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @param s String to test.
     * @return {@code true} if the provided string is an integer within the
     * specified bounds. Else, {@code false} is returned.
     */
    @Override
    public boolean test(final String s) {
        if (s.matches("[0-9]+")) {

            try {
                final int v = Integer.parseInt(s);
                return (v > min) && (v < max);

                // Value escaped integer bounds
            } catch (final NumberFormatException e) {
                return false;
            }

            // Not an integer
        } else {
            return false;
        }
    }
}
