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

    /*
     * This Class may not be required; but I think it's still nice to be able
     *  to set up the GridPane in a separate Class (things such as ActionEvents)
     *  which can the just be initialised when needed.
     */

    /**
     * Constructs a Grid Pane of a final size and state.
     *
     * @param minMaxRows Minimum Rows and the Maximum Rows.
     * @param minMaxCols Minimum Cols and Maximum Cols
     * @return GridPane ready to use.
     */
    GridPane supply(final int minMaxRows, final int minMaxCols);
}
