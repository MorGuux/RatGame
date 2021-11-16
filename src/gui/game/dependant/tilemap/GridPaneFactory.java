package gui.game.dependant.tilemap;

import javafx.scene.layout.GridPane;

/**
 * Factory template for constructing a GridPane of a Fixed size.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface GridPaneFactory {

    /**
     * Constructs a Grid Pane of a final size and state.
     *
     * @param minMaxRows Minimum Rows and the Maximum Rows.
     * @param minMaxCols Minimum Cols and Maximum Cols
     * @return GridPane ready to use.
     */
    GridPane supply(final int minMaxRows, final int minMaxCols);
}
