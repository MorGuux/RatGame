package game.classinfo.tags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Java annotation created on 24/02/2022 for usage in project RatGame-A2.
 * Annotation used to expand the understanding of existing Entities so that
 * we can create Tooltips which explain what the class does and how to use it.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClassDescription {

    /**
     * @return Description of the class, what it does, and how to use it.
     */
    String description();
}
