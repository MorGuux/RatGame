package game.generator;

/**
 * Item Generator Factory template for creating a new instance of the target
 * Item.
 *
 * @param <T> The item this factory object creates.
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface ItemFactory<T> {

    /**
     * Create an instance of the item from the given Row and Column values.
     *
     * @param row Row this item should exist at.
     * @param col Column this item should exist at.
     * @return Newly created item at the provided position.
     */
    T create(int row, int col);
}
