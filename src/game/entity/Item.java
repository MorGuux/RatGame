package game.entity;

public abstract class Item extends Entity {

    /**
     * Construct an Entity from the base starting x and y value.
     *
     * @param initX X position in a 2D Array.
     * @param initY Y position in a 2D Array.
     */
    public Item(int initX, int initY) {
        super(initX, initY);
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initX     X position in a 2D Array.
     * @param initY     Y position in a 2D Array.
     * @param curHealth Current health of the Entity.
     */
    public Item(int initX, int initY, int curHealth) {
        super(initX, initY, curHealth);
    }
}
