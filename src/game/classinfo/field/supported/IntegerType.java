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

    /**
     * Wraps the primitive integer type.
     */
    INT_P(int.class,
            (a) -> Integer.parseInt(a[0])
    ),

    /**
     * Wraps the Integer class. Which ironically wraps the primitive type.
     */
    INT_C(Integer.class,
            (a) -> Integer.parseInt(a[0])
    ),

    /**
     * Wraps the long primitive type.
     */
    LONG_P(long.class,
            (a) -> Long.parseLong(a[0])
    ),

    /**
     * Wraps the Long class type wrapper.
     */
    LONG_C(Long.class,
            (a) -> Long.parseLong(a[0])
    ),

    /**
     * Wraps the Atomic Long class type.
     */
    ATOMIC_LONG(AtomicLong.class,
            (a) -> new AtomicLong(Long.parseLong(a[0]))
    ),

    /**
     * Wraps the Atomic Integer class type.
     */
    ATOMIC_INT(AtomicInteger.class,
            (a) -> new AtomicLong(Long.parseLong(a[0]))
    );

    /**
     * Regex used by the Unary operator, this just allows text fields to have
     * enforced chars.
     */
    private static final String INTEGER_INTERMEDIATE_REGEX
            = "-?[0-9]*";

    /**
     * Regex that matches a complete integer string. I.e., a potentially
     * signed integer -123456; though still limited to the scope of type i.e
     * ., {@link Integer#MAX_VALUE} < {@link Long#MAX_VALUE} this comparison
     * will have to be manually handled.
     */
    private static final String INTEGER_COMPLETE_REGEX
            = "-?[0-9]+";

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
     * @param targetClass The target class of this ordinal.
     * @param factoryInst Factory template that can construct new instances of
     *                    the target type.
     */
    IntegerType(final Class<?> targetClass,
                final GenericFactory<?> factoryInst) {
        this.target = targetClass;
        this.factory = factoryInst;
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
