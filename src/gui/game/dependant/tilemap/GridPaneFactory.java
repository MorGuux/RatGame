package gui.game.dependant.tilemap;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory template for constructing a GridPane of a Fixed size.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface GridPaneFactory {

    /**
     * GridPane factory wrapper to create GridPanes of size Row, Col all
     * setup for Center alignment and resizing.
     *
     * @author -Ry
     * @version 0.1
     * Copyright: N/A
     */
    class CenteredGridPane implements GridPaneFactory {

        /**
         * Constructs a Grid Pane of a final size and state.
         *
         * @param rows Minimum Rows and the Maximum Rows.
         * @param cols Minimum Cols and Maximum Cols
         * @return GridPane ready to use.
         */
        @Override
        public GridPane supply(final int rows,
                               final int cols) {
            final GridPane pane = new GridPane();
            pane.getColumnConstraints().clear();
            pane.getRowConstraints().clear();

            pane.getRowConstraints().addAll(getRowConstraints(rows));
            pane.getColumnConstraints().addAll(getColumnConstraints(cols));

            return pane;
        }

        private List<ColumnConstraints> getColumnConstraints(final int count) {
            final List<ColumnConstraints> list = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                final ColumnConstraints col = new ColumnConstraints();
                col.setHgrow(Priority.ALWAYS);
                col.setHalignment(HPos.CENTER);
                col.setMaxWidth(ConstraintsBase.CONSTRAIN_TO_PREF);
                col.setMinWidth(ConstraintsBase.CONSTRAIN_TO_PREF);

                list.add(col);
            }

            return list;
        }

        private List<RowConstraints> getRowConstraints(final int count) {
            final List<RowConstraints> list = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                final RowConstraints row = new RowConstraints();
                row.setVgrow(Priority.ALWAYS);
                row.setValignment(VPos.CENTER);
                row.setMaxHeight(ConstraintsBase.CONSTRAIN_TO_PREF);
                row.setMinHeight(ConstraintsBase.CONSTRAIN_TO_PREF);

                list.add(row);
            }

            return list;
        }
    }
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
