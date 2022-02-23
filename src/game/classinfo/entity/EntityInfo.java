package game.classinfo.entity;

import game.classinfo.ClassInfo;
import game.classinfo.field.Type;
import game.classinfo.tags.BlackListed;
import game.classinfo.tags.DisplaySpriteResource;
import game.classinfo.tags.WritableField;
import game.entity.Entity;
import game.tile.Tile;
import javafx.util.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
 * @version 0.4
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

    /**
     * @return {@code true} if the target entity is hostile to the player.
     */
    public boolean isHostile() {
        return this.constructEntity(0, 0).isHostile();
    }

    /**
     * Gets the friendly name of the provided field, if one exists.
     *
     * @param f The field to obtain the friendly name for.
     * @return Friendly name if present, else the fields name.
     */
    public String getNameFor(final Field f) {
        final Class<WritableField> anno = WritableField.class;
        if (f.isAnnotationPresent(anno)) {
            return f.getAnnotation(anno).name();

        } else {
            return f.getName();
        }
    }

    /**
     * Gets the default value for the provided field.
     *
     * @param f The field to get the default value for.
     * @return The default value, if one exists.
     */
    public String getDefaultValueFor(final Field f) {
        final Class<WritableField> anno = WritableField.class;
        if (f.isAnnotationPresent(anno)) {
            return f.getAnnotation(anno).defaultValue();

        } else {
            return "Unknown Default Value...";
        }
    }

    /**
     * Gets the value of the provided field in the target object instance.
     *
     * @param e The entity to get the field value from.
     * @param f The literal field in Entity to obtain.
     * @return Optional of the value held; if the value is null, or if the
     * value could not be obtained from the provided entity then an Empty
     * optional is returned. Else, the value wrapped in an optional is returned.
     */
    public Optional<Object> getCurrentValue(final Entity e,
                                            final Field f) {
        f.setAccessible(true);

        try {
            final Object o = f.get(e);

            if (o == null) {
                return Optional.empty();
            } else {
                return Optional.of(o);
            }
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /**
     * Collects all blacklisted tiles for the target entity.
     *
     * @return An array of potentially 0 items.
     */
    public Class<?>[] getBlackListedTiles() {
        final Field[] found = getAllFieldsAnnotated(BlackListed.class);

        final List<Class<?>> blacklisted = new ArrayList<>();
        for (final Field f : found) {

            // We only care about static arrays
            if (Modifier.isStatic(f.getModifiers())
                    && f.getType().equals(Class[].class)) {
                f.setAccessible(true);

                // Collect the classes
                try {
                    final Object o = f.get(null);
                    if (o instanceof final Class<?>[] items) {
                        blacklisted.addAll(Arrays.asList(items));
                    }

                    // Shouldn't happen
                } catch (final IllegalAccessException e) {
                    e.printStackTrace();
                    return new Class[0];
                }
            }
        }

        return blacklisted.toArray(new Class[0]);
    }

    /**
     * Tests if the provided class is blacklisted for the target entity.
     *
     * @param clazz The class to test.
     * @return {@code true} if the target class is blacklisted. Else, if not
     * {@code false} is returned.
     */
    public boolean isBlacklistedTile(final Class<? extends Tile> clazz) {
        return Arrays.asList(this.getBlackListedTiles()).contains(clazz);
    }

    /**
     * @return Row Field type in {@link Entity}.
     * @throws IllegalStateException If Entity doesn't have a field called row.
     */
    private Field getRowField() {
        try {
            return Entity.class.getDeclaredField("row");
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return Col Field type in {@link Entity}.
     * @throws IllegalStateException If Entity doesn't have a field called col.
     */
    private Field getColField() {
        try {
            return Entity.class.getDeclaredField("col");
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Constructs a new instance of the target entity using the Field Object
     * pair list as the entity specific data.
     *
     * @param fieldData The data to set.
     * @return Newly constructed, and set entity instance.
     * @throws InstantiationException If one or more of the provided field
     *                                types does not apply to Entity or if there
     *                                aren't enough relevant arguments to
     *                                construct the target entity safely.
     */
    public Entity constructEntity(final List<Pair<Field, Object>> fieldData)
            throws InstantiationException {

        final Field row = getRowField();
        final Field col = getColField();
        int rowV = -1;
        int colV = -1;

        boolean rowFound = false;
        boolean colFound = false;
        for (final Pair<Field, Object> pair : fieldData) {
            final Field cur = pair.getKey();
            final Object v = pair.getValue();

            if (cur.equals(row)) {
                rowV = (int) v;
                rowFound = true;
            }

            if (cur.equals(col)) {
                colV = (int) v;
                colFound = true;
            }

            // If col and row found just create the entity
            if (colFound && rowFound) {
                final Entity e = this.constructEntity(rowV, colV);
                fieldData.forEach((p) -> {
                    try {
                        p.getKey().setAccessible(true);
                        p.getKey().set(e, p.getValue());
                    } catch (final IllegalAccessException ex) {
                        throw new IllegalStateException(ex);
                    }
                });
                return e;
            }
        }

        // If we reached this point we messed up somewhere.
        throw new InstantiationException(
                "Failed to instantiate the target entity: "
                        + getTargetClass().getSimpleName()
        );
    }
}
