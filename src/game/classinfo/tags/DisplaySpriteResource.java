package game.classinfo.tags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Java annotation created on 10/02/2022 for usage in project RatGame-A2.
 * Used to annotate some static URL image resource.
 *
 * @author -Ry
 * @version 0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DisplaySpriteResource {
}
