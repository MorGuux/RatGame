package game.classinfo.tags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Java annotation created on 18/02/2022 for usage in project RatGame-A2.
 * Used to annotate a static field that consists of potentially many
 * Blacklisted Tile objects.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BlackListed {
}
