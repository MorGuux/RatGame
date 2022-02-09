package game.classinfo.field;

import java.util.Arrays;

/**
 * Java class created on 09/02/2022 for usage in project RatGame-A2.
 * Exception wraps all instantiation exceptions that can occur from
 * {@link Type#construct(String...)} providing the needed information to
 * deduce further any issues.
 *
 * @author -Ry
 */
public class TypeInstantiationException extends Exception {

    /**
     * Constructs a type instantiation exception using the type and args as
     * the basis for the exception.
     *
     * @param t    The type that failed to construct.
     * @param args The args that were used in the construction attempt.
     */
    public TypeInstantiationException(final Type t,
                                      final String... args) {
        super(String.format(
                "Failed to instantiate the type: [%s] using the following "
                        + "args: [%s]",
                getTypeInfo(t),
                Arrays.deepToString(args)
        ));
    }

    /**
     * Gets some information about the provided type.
     *
     * @param t The type to probe.
     * @return Formatted string.
     */
    private static String getTypeInfo(final Type t) {
        return String.format("%s; %s",
                t.getClass().getSimpleName(),
                t.getTarget().getSimpleName()
        );
    }
}
