package game.classinfo.field.supported;

import game.classinfo.field.GenericFactory;
import game.classinfo.field.Type;
import javafx.scene.control.TextFormatter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

/**
 * Java enum created on 04/02/2022 for usage in project RatGame-LevelBuilder.
 *
 * @author -Ry
 */
public enum BooleanType implements Type {

    BOOLEAN_P(
            boolean.class,
            (s) -> Boolean.parseBoolean(s[0])
    ),

    BOOLEAN_C(
            Boolean.class,
            (s) -> Boolean.parseBoolean(s[0])
    ),

    ATOMIC_BOOLEAN(
            AtomicBoolean.class,
            (s) -> new AtomicBoolean(Boolean.parseBoolean(s[0]))
    );

    private static final String BOOLEAN_INTERMEDIATE_REGEX
            = "(?i)|t|tr|tru|true|f|fa|fal|fals|false";
    private static final String BOOLEAN_COMPLETE_REGEX
            = "(?i)false|true";

    private final Class<?> target;
    private final GenericFactory<?> factory;

    BooleanType(final Class<?> type,
                final GenericFactory<?> factory) {
        this.target = type;
        this.factory = factory;
    }

    /**
     * Creates an instance of the target type from the provided arguments.
     *
     * @param args The args to use to create this type.
     * @return Newly constructed Instance.
     */
    @Override
    public Object construct(final String... args) {
        return factory.create(args);
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