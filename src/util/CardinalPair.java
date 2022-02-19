package util;

import java.util.Optional;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2. Cardinal
 * pair object which maps in all cardinal directions around a root object.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public abstract class CardinalPair<C, N, E, S, W> {

    ///////////////////////////////////////////////////////////////////////////
    // Lmao this class is hella overkill.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Object deriving the center of the cardinality.
     */
    private final C center;

    /**
     * North of the center.
     */
    private final N north;

    /**
     * East of the center.
     */
    private final E east;

    /**
     * South of the center.
     */
    private final S south;

    /**
     * West of the center.
     */
    private final W west;

    /**
     * Constructs the cardinal pair from the given params.
     *
     * @param c Center of the cardinal, nullable.
     * @param n North of the center, nullable.
     * @param e East of the center, nullable.
     * @param s South of the center, nullable.
     * @param w West of the center, nullable.
     */
    public CardinalPair(final C c,
                        final N n,
                        final E e,
                        final S s,
                        final W w) {
        this.center = c;
        this.north = n;
        this.east = e;
        this.south = s;
        this.west = w;
    }

    /**
     * Wraps the provided argument in an optional.
     *
     * @param f   The argument to wrap in an optional.
     * @param <F> The type of the argument.
     * @return Empty optional if the argument is null, else an optional of
     * the provided argument is returned.
     */
    private static <F> Optional<F> optionalWrap(final F f) {
        if (f == null) {
            return Optional.empty();
        } else {
            return Optional.of(f);
        }
    }

    /**
     * @return If the center is {@code null} then an Empty optional is
     * returned. Else, if present then an Optional consisting of the center
     * is returned.
     */
    public Optional<C> getCenter() {
        return optionalWrap(this.center);
    }

    /**
     * @return If North is {@code null} then an Empty optional is
     * returned. Else, if present then an Optional consisting of the North
     * element is returned.
     */
    public Optional<N> getNorth() {
        return optionalWrap(this.north);
    }

    /**
     * @return If East is {@code null} then an Empty optional is
     * returned. Else, if present then an Optional consisting of the East
     * element is returned.
     */
    public Optional<E> getEast() {
        return optionalWrap(this.east);
    }

    /**
     * @return If South is {@code null} then an Empty optional is
     * returned. Else, if present then an Optional consisting of the South
     * element is returned.
     */
    public Optional<S> getSouth() {
        return optionalWrap(this.south);
    }

    /**
     * @return If West is {@code null} then an Empty optional is
     * returned. Else, if present then an Optional consisting of the West
     * element is returned.
     */
    public Optional<W> getWest() {
        return optionalWrap(this.west);
    }
}
