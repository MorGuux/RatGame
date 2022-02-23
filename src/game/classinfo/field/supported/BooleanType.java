package game.classinfo.field.supported;

import game.classinfo.field.EnumerableValue;
import game.classinfo.field.GenericFactory;
import game.classinfo.field.Type;
import game.classinfo.field.TypeInstantiationException;
import javafx.scene.control.TextFormatter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

/**
 * Java enum created on 04/02/2022 for usage in project RatGame-LevelBuilder.
 *
 * @author -Ry
 */
public enum BooleanType implements Type {

    /**
     * Wraps a primitive boolean type.
     */
    BOOLEAN_P(
            boolean.class,
            (s) -> Boolean.parseBoolean(s[0])
    ),

    /**
     * Wraps an Object boolean type.
     */
    BOOLEAN_C(
            Boolean.class,
            (s) -> Boolean.parseBoolean(s[0])
    ),

    /**
     * Wraps the atomic boolean class.
     */
    ATOMIC_BOOLEAN(
            AtomicBoolean.class,
            (s) -> new AtomicBoolean(Boolean.parseBoolean(s[0]))
    );

    /**
     * Represents all the possible boolean types.
     *
     * @author -Ry
     * @version 0.3
     * Copyright: N/A
     */
    public enum BooleanEnumerable implements EnumerableValue {

        ////////////////////////////////////////////////////////////////////////
        // Stuff that would exist here are values that represent some actual
        // value for example yes, y, and true all represent true, and so they
        // are enumerable value choices.
        ////////////////////////////////////////////////////////////////////////

        /**
         * Enumerable value for True state.
         */
        TRUE(),

        /**
         * Enumerable value for false state.
         */
        FALSE();

        /**
         * @return Constructs this enumerable value.
         */
        @Override
        public Object construct(final Type t) {
            try {
                return t.construct(String.valueOf(this.equals(TRUE)));

                // We know this can't happen
            } catch (final TypeInstantiationException e) {
                return false;
            }
        }
    }

    /**
     * Regex used by the Unary operator, this just allows text fields to have
     * enforced chars.
     */
    private static final String BOOLEAN_INTERMEDIATE_REGEX
            = "(?i)|t|tr|tru|true|f|fa|fal|fals|false";

    /**
     * Regex that matches a complete boolean string.
     */
    private static final String BOOLEAN_COMPLETE_REGEX
            = "(?i)false|true";

    /**
     * The target class type of the enumerated type.
     */
    private final Class<?> target;

    /**
     * Factory template that will construct new instances of the target
     * enumerated type (Through the construction mechanism
     * {@link #construct(String...)}).
     */
    private final GenericFactory<?> factory;

    /**
     * Enumeration constructor.
     *
     * @param type Class type that this entry wraps.
     * @param fact Factory template that can construct new instances of
     *             the wrapped type.
     */
    BooleanType(final Class<?> type,
                final GenericFactory<?> fact) {
        this.target = type;
        this.factory = fact;
    }

    /**
     * Checks to see if the provided args are complete in the sense that
     * {@link #construct(String...)} will not throw an exception.
     *
     * @param args The args to test.
     * @return {@code true} if the args are safe to use.
     */
    @Override
    public boolean isComplete(final String... args) {
        return args[0].matches(BOOLEAN_COMPLETE_REGEX);
    }

    /**
     * Used specifically for a JavaFX scene this enforces that some field
     * only contain the correct data.
     *
     * @return Text Formatter for the target type.
     */
    @Override
    public UnaryOperator<TextFormatter.Change> getTextFieldHandler() {
        return (c) -> {
            // Probably a cleaner way to do this
            if (c.getControlNewText().matches(BOOLEAN_INTERMEDIATE_REGEX)) {
                return c;
            } else {
                return null;
            }
        };
    }

    /**
     * @return All possible enumerable values for the boolean type.
     */
    @Override
    public EnumerableValue[] getEnumerableValues() {
        return BooleanEnumerable.values();
    }

    /**
     * Gets the enumerable representation of the provided object.
     *
     * @param o The object to get the enumerable for.
     * @return The enumerable of the target object, if one exists. else null.
     */
    @Override
    public EnumerableValue enumerableOf(final Object o) {
        Object v = null;
        if (o instanceof final AtomicBoolean a) {
            v = a.get();
        }

        if (v instanceof final Boolean b) {
            if (b) {
                return BooleanEnumerable.TRUE;
            } else {
                return BooleanEnumerable.FALSE;
            }
        } else {
            return null;
        }
    }

    /**
     * @return The class type of this type.
     */
    @Override
    public Class<?> getTarget() {
        return this.target;
    }

    /**
     * @return Factory object that can be used to construct this type.
     */
    @Override
    public GenericFactory<?> getFactory() {
        return this.factory;
    }
}
