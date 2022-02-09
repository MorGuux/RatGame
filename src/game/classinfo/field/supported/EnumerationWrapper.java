package game.classinfo.field.supported;

import game.classinfo.field.GenericFactory;
import game.classinfo.field.Type;
import game.entity.subclass.rat.Rat;
import javafx.scene.control.TextFormatter;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Java enum created on 04/02/2022 for usage in project RatGame-LevelBuilder.
 *
 * @author -Ry
 */
public enum EnumerationWrapper implements Type {

    /**
     * Wraps the Rat Sex enumeration type.
     */
    RAT_SEX_TYPE(Rat.Sex.class, (s) -> {
        if (s[0].equalsIgnoreCase("m")) {
            return Rat.Sex.MALE;
        } else {
            return Rat.Sex.FEMALE;
        }
    }, (e) -> {
        if (e.getControlNewText().matches("(?i)|m|f")) {
            return e;
        } else {
            return null;
        }
    }, (s) -> s[0].matches("(?i)M|F")),

    /**
     * Wraps the Rat Age enumeration type.
     */
    RAT_AGE_TYPE(Rat.Age.class, (s) -> {
        if (s[0].matches("(?i)a|ad|adu|adul|adult")) {
            return Rat.Age.ADULT;
        } else {
            return Rat.Age.BABY;
        }
    }, (e) -> {
        if (e.getControlNewText().matches(
                "(?i)|a|ad|adu|adul|adult|b|ba|bab|baby")) {
            return e;
        } else {
            return null;
        }
    }, (s) -> s[0].matches("(?i)ADULT|BABY"));

    private final Class<?> target;
    private final GenericFactory<?> factory;
    private final UnaryOperator<TextFormatter.Change> textFormat;
    private final Function<String[], Boolean> argsVerifier;

    EnumerationWrapper(final Class<?> target,
                       final GenericFactory<?> factory,
                       final UnaryOperator<TextFormatter.Change> textFormat,
                       final Function<String[], Boolean> verifier) {
        this.target = target;
        this.factory = factory;
        this.textFormat = textFormat;
        this.argsVerifier = verifier;
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
        return argsVerifier.apply(args);
    }

    /**
     * Used specifically for a JavaFX scene this enforces that some field
     * only contain the correct data.
     *
     * @return Text Formatter for the target type.
     */
    @Override
    public UnaryOperator<TextFormatter.Change> getTextFieldHandler() {
        return this.textFormat;
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
