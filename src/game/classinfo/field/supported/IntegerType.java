package game.classinfo.field.supported;

import game.classinfo.field.GenericFactory;
import game.classinfo.field.Type;
import javafx.scene.control.TextFormatter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

/**
 * Java enum created on 04/02/2022 for usage in project RatGame-LevelBuilder.
 * This wraps all the basic Integer style types used within Java.
 *
 * @author -Ry
 */
public enum IntegerType implements Type {
    INT_P(int.class,
            (a) -> Integer.parseInt(a[0])
    ),

    INT_C(Integer.class,
            (a) -> Integer.parseInt(a[0])
    ),

    LONG_P(long.class,
            (a) -> Long.parseLong(a[0])
    ),

    LONG_C(Long.class,
            (a) -> Long.parseLong(a[0])
    ),

    ATOMIC_LONG(AtomicLong.class,
            (a) -> new AtomicLong(Long.parseLong(a[0]))
    ),

    ATOMIC_INT(AtomicInteger.class,
            (a) -> new AtomicLong(Long.parseLong(a[0]))
    );

    private static final String INTEGER_INTERMEDIATE_REGEX
            = "-?[0-9]*";
    private static final String INTEGER_COMPLETE_REGEX
            = "-?[0-9]+";

    private final Class<?> target;
    private final GenericFactory<?> factory;

    IntegerType(final Class<?> target,
                final GenericFactory<?> factory) {
        this.target = target;
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
        return args[0].matches(INTEGER_COMPLETE_REGEX);
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
            if (c.getControlNewText().matches(INTEGER_INTERMEDIATE_REGEX)) {
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