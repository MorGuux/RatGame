package game.classinfo.field;

/**
 * Java main class created on 09/02/2022 for usage in
 * project RatGame-A2.
 *
 * @param <T> The type that this factory creates.
 * @author -Ry
 */
public interface GenericFactory<T> {

    /**
     * @param args Arguments used to construct the target type.
     * @return Newly constructed instance of T.
     */
    T create(String... args);
}
