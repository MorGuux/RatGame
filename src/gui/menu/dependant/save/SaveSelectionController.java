package gui.menu.dependant.save;

import game.entity.Entity;
import game.level.reader.RatGameSaveFile;
import game.level.reader.module.GameProperties;
import game.player.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scene wraps a series of RatGameSaveFiles that can be selected. The
 * selection which may not exist can then be extracted via get methods.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class SaveSelectionController {

    /**
     * The underlying XML scene object hierarchy.
     */
    private static final URL SCENE_FXML =
            SaveSelectionController.class.getResource(
                    "SaveSelectionScene.fxml"
            );

    /**
     * Error message for when the stage could not be loaded due to an IO
     * Exception.
     */
    private static final String ERR_STAGE_LOAD_FAILED = "Could not load the "
            + "Save Selection Scene as required resource was not accessible.";
    /**
     * The root node for the scene.
     */
    @FXML
    private BorderPane root;
    /**
     * Table that contains all the save files and their relevant information.
     */
    @FXML
    private TableView<SaveWrapper> selectionTableView;
    /**
     * The columns available for the table view.
     */
    @FXML
    private TableColumn<SaveWrapper, ?> levelNameCol;
    @FXML
    private TableColumn<SaveWrapper, ?> playerNameCol;
    @FXML
    private TableColumn<SaveWrapper, ?> playerScoreCol;
    @FXML
    private TableColumn<SaveWrapper, ?> playerTimePlayedCol;
    @FXML
    private TableColumn<SaveWrapper, ?> numberOfRatsCol;
    /**
     * The level selection that the user has made.
     */
    private SaveWrapper selection;

    /**
     * Gets the controller of the scene which is ready to be displayed with
     * all the saves provided inserted ready to be selected.
     *
     * @param saves The save files that the scene should display for selection.
     * @return The controller of the scene ready to be displayed in a stage
     * or embedded into a scene.
     * @throws IllegalStateException If the scene fails to load correctly.
     */
    public static SaveSelectionController loadAndGet(
            final List<RatGameSaveFile> saves) {

        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);
        try {
            loader.load();
            final SaveSelectionController cont = loader.getController();
            cont.setup(saves);

            return cont;
        } catch (IOException e) {
            throw new IllegalStateException(
                    ERR_STAGE_LOAD_FAILED
            );
        }
    }

    /**
     * Add all the save files for selection to the selection view.
     *
     * @param saves The saves that are to be added for selection.
     */
    private void setup(final List<RatGameSaveFile> saves) {
        saves.forEach(i -> {
            selectionTableView.getItems().add(new SaveWrapper(i));
        });

        levelNameCol.setCellValueFactory(
                new PropertyValueFactory<>("levelName")
        );
        playerNameCol.setCellValueFactory(
                new PropertyValueFactory<>("playerName")
        );
        playerTimePlayedCol.setCellValueFactory(
                new PropertyValueFactory<>("timePlayed")
        );
        playerScoreCol.setCellValueFactory(
                new PropertyValueFactory<>("playerScore")
        );
        numberOfRatsCol.setCellValueFactory(
                new PropertyValueFactory<>("numRats")
        );
    }

    /**
     * @return The root node for the scene.
     */
    public BorderPane getRoot() {
        return root;
    }

    /**
     * Gets the confirmed selection item. If no item was or has been selected
     * or the selection was cancelled then this will return an empty optional.
     *
     * @return Optional of the item that was selected.
     */
    public Optional<RatGameSaveFile> getSelection() {
        if (this.selection == null) {
            return Optional.empty();

        } else {
            return Optional.of(selection.saveFile);
        }
    }

    /**
     * Cancels the selection and terminates the window.
     */
    @FXML
    private void onCancelSelection() {
        this.selection = null;
        this.root.getScene().getWindow().hide();
    }

    /**
     * Confirms the currently selected item as the save file to return. If no
     * item is selected then the result will be empty.
     */
    @FXML
    private void onConfirmSelection() {
        this.selection =
                selectionTableView.getSelectionModel().getSelectedItem();
        this.root.getScene().getWindow().hide();
    }

    /**
     * Internal wrapper class that allows the data of a Save file to be
     * displayed without modifying the base save class.
     *
     * @author -Ry
     * @version 0.1
     * Copyright: N/A
     */
    public static class SaveWrapper {

        /**
         * The target save file that is being wrapped.
         */
        private final RatGameSaveFile saveFile;

        /**
         * Constructs from a save file.
         *
         * @param save The save file to construct from.
         */
        public SaveWrapper(final RatGameSaveFile save) {
            this.saveFile = save;
        }

        /**
         * @return Level name for the save.
         */
        public String getLevelName() {
            return this.getProperties().getLevelName();
        }

        /**
         * @return Player Name for the save.
         */
        public String getPlayerName() {
            return this.getPlayer().getPlayerName();
        }

        /**
         * @return The total time played for the user.
         */
        public int getTimePlayed() {
            final int timeFactor = 1000;
            return this.getPlayer().getPlayTime() / timeFactor;
        }

        /**
         * @return The players current score.
         */
        public int getPlayerScore() {
            return this.getPlayer().getCurrentScore();
        }

        /**
         * @return The default properties for the save file.
         */
        public GameProperties getProperties() {
            return this.getSaveFile().getDefaultProperties();
        }

        /**
         * @return The current player of the save file.
         */
        public Player getPlayer() {
            return this.getSaveFile().getPlayer();
        }

        /**
         * @return The full save file used to construct this.
         */
        public RatGameSaveFile getSaveFile() {
            return saveFile;
        }

        /**
         * Counts the number of entities marked as hostile through
         * {@link Entity#isHostile()}.
         *
         * @return The number of hostile entities.
         */
        public int getNumRats() {
            final AtomicInteger count = new AtomicInteger();

            getSaveFile().getEntityPositionMap().forEach((e, pos) -> {
                if (e.isHostile()) {
                    count.getAndIncrement();
                }
            });

            return count.get();
        }
    }
}
