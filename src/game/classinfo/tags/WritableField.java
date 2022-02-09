package game.classinfo.tags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Java annotation created on 09/02/2022 for usage in project RatGame-A2.
 * This represents a field of some class that can be written to.
 *
 * @author -Ry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WritableField {

    /**
     * @return The safe name for this field.
     */
    String name();

    /**
     * @return The default value held for this field.
     */
    String defaultValue();
}
