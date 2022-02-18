package game.classinfo.field;

/**
 * Java interface created on 18/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public interface EnumerableValue {

    /**
     * @param t The parent type of this enumerable value.
     * @return Constructs this enumerable value.
     */
    default Object construct(final Type t) {
        return this;
    }
}
