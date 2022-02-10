package game.classinfo.tags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Java annotation created on 09/02/2022 for usage in project RatGame-A2.
 * Annotation used to annotate constructors of relevance.
 *
 * @author -Ry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface TargetConstructor {
    // Nothing belongs here
}
