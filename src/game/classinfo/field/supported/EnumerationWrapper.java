package game.classinfo.field.supported;

import game.classinfo.field.GenericFactory;
import game.classinfo.field.Type;
import game.entity.subclass.rat.Rat;
import javafx.scene.control.TextFormatter;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Java enum created on 04/02/2022 for usage in project RatGame-LevelBuilder.
 * Wraps the enumerated types used in the Rat game and allows simplistic
 * construction mechanisms without really caring about the input data and or
 * how its obtained.
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
     * Text format for the String[] that
     * {@link #factory#construct(String...)} relies on.
     */
    private final UnaryOperator<TextFormatter.Change> textFormat;

    /**
     * Function used to verify some String[] args to the target enumerated type.
     */
    private final Function<String[], Boolean> argsVerifier;

    /**
     * Enumeration wrapper constructor.
     *
     * @param target     The target class of this ordinal.
     * @param factory    The factory template that can construct new instances
     *                   of the target class.
     * @param textFormat The text formatter for JavaFX scenes.
     * @param verifier   The arg verifier function that safely discerns
     *                   some String[] to the target type.
     */
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
