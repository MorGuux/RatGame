package gui.game.dependant.tilemap;

import javafx.scene.layout.GridPane;

/**
 * Game Map that is to be
 */
public class GameMap {

    private final GridPane root;
    private final int rowCount;
    private final int colCount;

    public GameMap(final int rows,
                   final int cols) {
        this.rowCount = rows;
        this.colCount = cols;

        this.root = new GridPane();
    }
}
