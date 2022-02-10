package game.classinfo.entity;

import game.classinfo.ClassInfo;

/**
 * Java class created on 10/02/2022 for usage in project RatGame-A2. Used to
 * push up runtime exceptions so that they can be forcefully handled.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class MalformedWritableClassException extends Exception {

    /**
     * Exception constructor which creates the base error message from the
     * provided params.
     *
     * @param target The class that is malformed.
     * @param e The exception that was produced in its malformed state.
     */
    public MalformedWritableClassException(final Class<?> target,
                                           final Exception e) {
        super(String.format(
                "The class [%s] is malformed or not setup for usage with [%s]"
                        + " See: [%s]",
                target.getSimpleName(),
                ClassInfo.class.getSimpleName(),
                e.getMessage()
        ));
    }
}
