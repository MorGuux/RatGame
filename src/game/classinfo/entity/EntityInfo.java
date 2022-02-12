package game.classinfo.entity;

import game.classinfo.ClassInfo;
import game.classinfo.field.Type;
import game.classinfo.tags.DisplaySpriteResource;
import game.entity.Entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Java class created on 10/02/2022 for usage in project RatGame-A2. This is
 * an abstraction from the base {@link ClassInfo} class which pulls further
 * away from the reflection nature of the class. Simplifying the options and
 * exception handling.
 *
 * @param <T> Entity subclass we are obtaining class info for.
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class EntityInfo<T extends Entity> extends ClassInfo<T> {

    /**
     * Expected number of arguments that the TargetConstructor should have.
     */
    private static final int EXPECTED_PARAM_COUNT = 2;

    /**
     * The constructor that will be used to create new instances of the
     * target entity.
     */
    private final Constructor<?> cons;

    /**
     * The display sprite image resource of target entity.
     */
    private final URL displaySprite;

    /**
     * Map of all the Writable Fields in the target entity mapped to its Type
     * object. This allows us to create new instances and also auto-generate
     * forms based on the Type alone.
     */
    private final Map<Field, Type> writableFieldMap;

    /**
     * Constructs a new instance of the Class Info for the target class.
     *
     * @param target The target class of this class info.
     * @throws NullPointerException If any parameter is null.
     */
    public EntityInfo(final Class<T> target)
            throws MalformedWritableClassException {
        super(target);

        try {
            // If no target constructor exists an exception will be thrown in
            // the super class
            cons = this.getTargetConstructor();

            // Collect constructor info
            final Class<?>[] params = cons.getParameterTypes();
            final Function<Class<?>, Boolean> typeTester
                    = (c) -> c.isAssignableFrom(int.class);

            // Ensure expected number of params
            if (params.length != EXPECTED_PARAM_COUNT) {

                // Ensure types match expected
                if (!Arrays.stream(params).allMatch(typeTester::apply)) {
                    final StringJoiner err = new StringJoiner(", ");
                    Arrays.stream(params).forEach(
                            i -> err.add(i.getSimpleName())
                    );
                    throw new IllegalStateException(String.format(
                            "Target Class: [%s] has an incorrect target "
                                    + "constructor should be [Int, Int] but is:"
                                    + " [%s].",
                            target.getSimpleName(),
                            err
                    ));
                }
            }

            // This only exists because it would be wasteful to re-call the
            // method. But this call is to push out the side effects. If a
            // class is malformed this would throw an exception
            writableFieldMap = super.getWritableFieldTypeMap();

            displaySprite = loadDisplaySprite();

            // Rethrow the exception under one that is forced to be handled
        } catch (final Exception e) {
            // Always stack trace
            e.printStackTrace();
            throw new MalformedWritableClassException(target, e);
        }
    }

    /**
     * Loads the display sprite from the target class.
     *
     * @return URL Display sprite for the target entity.
     * @throws IllegalAccessException If access to the Fields value is denied
     *                                in some way.
     * @throws NullPointerException   If the target sprite resource is null.
     */
    private URL loadDisplaySprite() throws IllegalAccessException {
        final Class<? extends Annotation> anno = DisplaySpriteResource.class;

        for (Field f : getAllFields()) {

            // We care about fields that are annotated, static, and hold a
            // URL value.
            if (f.isAnnotationPresent(anno)
                    && Modifier.isStatic(f.getModifiers())
                    && f.getType().isAssignableFrom(URL.class)) {

                // Set accessible just bypasses the private modifier (it does
                // a lot more than that as it bypasses java language checks)
                f.setAccessible(true);
                return Objects.requireNonNull((URL) f.get(null));
            }
        }

        // Default case throws an exception
        throw new IllegalAccessException(String.format(
                "Display sprite for Class [%s] not found...",
                getTargetClass().getSimpleName()
        ));
    }

    /**
     * Constructs a new instance of the target entity.
     *
     * @param row The starting row position of the entity.
     * @param col The starting column position of the entity.
     * @return Newly constructed entity.
     * @throws IllegalStateException If the target class somehow is a type
     *                               mismatch.
     */
    public T constructEntity(final int row,
                             final int col) {

        try {
            final Object o = cons.newInstance(row, col);

            if (this.getTargetClass().isInstance(o)) {
                return this.getTargetClass().cast(o);

                // This shouldn't happen since Class<T> == Class<T> in our
                // context.
            } else {
                throw new IllegalStateException(String.format(
                        "Type Mismatch, expected: [%s] but is [%s] which is "
                                + "un-assignable.",
                        this.getTargetClass().getSimpleName(),
                        o.getClass().getSimpleName()
                ));
            }

            // This can happen but is unlikely.
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * @return Display sprite URL image resource loaded from the target class.
     */
    public URL getDisplaySprite() {
        return displaySprite;
    }

    /**
     * Collects all the writable fields and maps them to their relevant
     * {@link Type} object which can be used to create new instances that the
     * field accepts as input to its {@link Field#set(Object, Object)} method.
     *
     * @return Newly constructed map.
     * @throws IllegalStateException If the target type has not got a
     *                               discernible wrapper class.
     */
    @Override
    public Map<Field, Type> getWritableFieldTypeMap() {
        return this.writableFieldMap;
    }
}
