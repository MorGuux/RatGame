package game.classinfo;

import game.classinfo.field.Type;
import game.classinfo.tags.TargetConstructor;
import game.classinfo.tags.WritableField;
import game.entity.Entity;
import game.entity.loader.EntityLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Java class created on 09/02/2022 for usage in project RatGame-A2. This
 * wraps some class and allows abstracted reflection based operations to be
 * performed.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public class ClassInfo<T> {

    /**
     * The target class that this class info wraps.
     */
    private final Class<T> targetClass;

    /**
     * Constructs a new instance of the Class Info for the target class.
     *
     * @param target The target class of this class info.
     * @throws NullPointerException If any parameter is null.
     */
    public ClassInfo(final Class<T> target) {
        this.targetClass = Objects.requireNonNull(target);
    }

    /**
     * Gets all declared fields leading up to its final super class.
     *
     * @return All fields that it inherits, excluding the fields of {@link
     * Object}
     */
    private Field[] getAllFields() {
        final List<Field> fields = new ArrayList<>();

        // Collects all fields leading up to its super class (This does not
        // consider Object as a super class)
        Class<?> target = targetClass;
        while (target != null) {

            // 0 adds the super class information at the top of the list
            fields.addAll(
                    0,
                    Arrays.stream(target.getDeclaredFields()).toList()
            );
            target = target.getSuperclass();
        }

        return fields.toArray(new Field[0]);
    }

    /**
     * Gets all fields of the target class that are annotated as writable.
     *
     * @return All fields that can be reflectively written.
     */
    public final Field[] getWritableFields() {
        final List<Field> writableFields = new ArrayList<>();
        for (final Field f : this.getAllFields()) {
            // Fields that are writable and not static
            if (f.isAnnotationPresent(WritableField.class)
                    && !Modifier.isStatic(f.getModifiers())) {
                writableFields.add(f);
            }
        }

        return writableFields.toArray(new Field[0]);
    }

    /**
     * @return Gets all declared constructors for the target class.
     */
    private Constructor<?>[] getAllConstructors() {
        return this.targetClass.getDeclaredConstructors();
    }

    /**
     * Gets the first constructor that is annotated with
     * {@link TargetConstructor} and then returns it.
     *
     * @return A constructor that can be used to construct new instances.
     */
    public final Constructor<?> getTargetConstructor() {
        for (final Constructor<?> v : getAllConstructors()) {
            if (v.isAnnotationPresent(TargetConstructor.class)) {
                return v;
            }
        }

        throw new IllegalStateException(createErrorMessage(
                "Get target constructor",
                "No valid constructor found."
        ));
    }

    /**
     * Creates a new instance of the target class this class info wraps. Does
     * so using the constructor from {@link #getTargetConstructor()}.
     *
     * @param args Args used to construct the object.
     * @return Newly constructed instance.
     * @throws InstantiationException If one occurs whilst attempting to
     *                                create the target instance.
     */
    public final T constructInstance(final Object... args)
            throws InstantiationException {
        final Constructor<?> cons = getTargetConstructor();
        cons.setAccessible(true);

        try {
            final Object o = cons.newInstance(args);

            // Assertion is not needed as we know it is an instance of the
            // target class; also would need to launch with -enableassertions
            assert targetClass.isInstance(o);
            return targetClass.cast(o);

        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {

            // Always stack trace
            e.printStackTrace();

            throw new InstantiationException(createErrorMessage(
                    "Construct Instance",
                    "Failed to construct an instance due to: "
                            + e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * Convenience method for iterating through all the writable fields of
     * the target class. This method calls
     * {@link Field#setAccessible(boolean)} with true before consuming.
     *
     * @param cons The action to apply to all fields.
     */
    public void forEachWritableField(final Consumer<Field> cons) {
        for (final Field f : getWritableFields()) {
            f.setAccessible(true);
            cons.accept(f);
        }
    }

    // Looks complicated but all this is doing is Mapping our Writable fields
    // to its target factory Type that allows us to create instances of the
    // target field type.

    /**
     * Collects all the writable fields and maps them to their relevant
     * {@link Type} object which can be used to create new instances that the
     * field accepts as input to its {@link Field#set(Object, Object)} method.
     *
     * @return Newly constructed map.
     * @throws IllegalStateException If the target type has not got a
     *                               discernible wrapper class.
     */
    public Map<Field, Type> getWritableFieldTypeMap() {
        // Linked map specifically because we want to keep insertion order
        final Map<Field, Type> fieldTypeList = new LinkedHashMap<>();
        final BiFunction<Field, Type, Boolean> typeTester
                = (f, t) -> f.getType().isAssignableFrom(t.getTarget());

        for (Field f : getWritableFields()) {

            boolean hasNoApplicableField = true;
            final Iterator<Type> iter
                    = Arrays.stream(Type.SUPPORTED_TYPES).iterator();

            // Find a Type that our field can be wrapped by
            while (hasNoApplicableField && iter.hasNext()) {
                final Type t = iter.next();
                if (typeTester.apply(f, t)) {
                    hasNoApplicableField = false;
                    fieldTypeList.put(f, t);
                }
            }

            // This shouldn't ever happen, but it is nice to know when it does.
            // As this tells us if we have set up a class wrong or have not
            // completed an intermediary step.
            if (hasNoApplicableField) {
                throw new IllegalStateException(String.format(
                        "Could not deduce target wrapper class for field "
                                + "with type [%s]",
                        f.getType().getSimpleName()
                ));
            }

        }

        return fieldTypeList;
    }

    /**
     * Constructs an error message using the provided args as the base.
     *
     * @param operation The operation that created an exception.
     * @param error     The error that was produced.
     * @return A complete error message.
     */
    private String createErrorMessage(final String operation,
                                      final String error) {
        return String.format(
                "[ERROR] :: Cannot perform operation [%s] as the error [%s] "
                        + "occurred in Class Info on target: [%s]",
                operation,
                error,
                targetClass.getSimpleName()
        );
    }

    public static void main(String[] args) {

        for (EntityLoader.ConstructableEntity c :
                EntityLoader.ConstructableEntity.values()) {

            ClassInfo<? extends Entity> info = new ClassInfo<>(c.getTarget());
            System.out.printf("\t[%s]\t%n", c.getTarget().getSimpleName());
            info.getWritableFieldTypeMap().forEach((f, t) -> {
                System.out.printf(
                        "FIELD=[%s; %s]; WrapperType=[%s]%n",
                        f.getName(),
                        f.getType().getSimpleName(),
                        t.getTarget().getSimpleName()
                );
            });
            System.out.println();
        }
    }
}
