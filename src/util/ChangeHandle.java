package util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class ChangeHandle<T, R> implements ChangeListener<Boolean> {

    /**
     * The target object that this handles.
     */
    private final R target;

    /**
     * The verification predicate to check for allowed values.
     */
    private final Predicate<T> verifier;

    /**
     * The value supplier which provides new values on an update that need to
     * be tested.
     */
    private final Supplier<T> valueSupplier;

    /**
     * Consumer action which updates the target object.
     */
    private final Consumer<T> stateUpdater;

    /**
     * The commit action for when an update is successful.
     */
    private final BiConsumer<R, T> commit;

    /**
     * Intermediate data value used as a basis for data rollback in the event
     * of an evaluation failure.
     */
    private T intermediateValue;

    /**
     * Constructs the change handle from the provided arguments.
     *
     * @param target       The target object to handle
     * @param handle       The predicate function which verifies changed data.
     * @param supplier     The function which supplies new data.
     * @param func         The function which sets/handles the data.
     * @param commitAction The commit action which is called whenever there
     *                     is a successful update to the target object.
     */
    public ChangeHandle(final R target,
                        final Predicate<T> handle,
                        final Supplier<T> supplier,
                        final Consumer<T> func,
                        final BiConsumer<R, T> commitAction) {
        this.target = target;
        this.verifier = handle;
        this.valueSupplier = supplier;
        this.stateUpdater = func;
        this.commit = commitAction;
    }

    /**
     * Changed action handler.
     *
     * @param original The original property.
     * @param old      The old value.
     * @param newValue The new value.
     */
    @Override
    public void changed(final ObservableValue<? extends Boolean> original,
                        final Boolean old,
                        final Boolean newValue) {

        // If focus obtained then record the current value
        if (newValue) {
            this.intermediateValue = valueSupplier.get();

            // If focus lost and values changed
        } else {
            if (!this.valueSupplier.get().equals(this.intermediateValue)) {

                // Test the new value, if bad use previous, if good use current.
                if (!this.verifier.test(valueSupplier.get())) {
                    this.stateUpdater.accept(intermediateValue);

                } else {
                    this.stateUpdater.accept(valueSupplier.get());
                    this.commit.accept(target, intermediateValue);
                }
            }
        }
    }
}
