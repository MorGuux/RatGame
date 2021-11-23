package game.entity;

/**
 * Item Class encapsulates Player, placed items. They don't differ at all
 * from regular Entities but are identifiably different.
 *
 * @author -Ry, Shashank Jain
 * @version 0.1
 * Copyright: N/A
 */
public abstract class Item extends Entity {

    /**
     * Construct an Entity from the base starting Row and Column.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     */
    public Item(final int initRow,
                final int initCol) {
        super(initRow, initCol);
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public Item(final int initialRow,
                final int initialCol,
                final int curHealth) {
        super(initialRow, initialCol, curHealth);
    }

    /**
     * Returns information about Item hostility. Since player aims to kill the
     * rats, Item is not considered hostile.
     * @return false
     */
    @Override
    public final boolean isHostile() {
        return false;
    }
}
