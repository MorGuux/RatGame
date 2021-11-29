package gui.menu.dependant.level;

import game.level.levels.RatGameLevel;
import game.level.reader.RatGameFile;
import game.level.reader.module.GameProperties;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 *
 */
public class LevelInputFormController implements Initializable {

    /**
     * The scene FXML of the object hierarchy.
     */
    private static final URL SCENE_FXML
            = LevelInputFormController.class.getResource("LoadLevelForm.fxml");

    /**
     * Columns for the table; we assign their attributes in the initialise
     * method.
     */
    @FXML
    private TableColumn<?, ?> levelNameCol;
    @FXML
    private TableColumn<?, ?> rowCountCol;
    @FXML
    private TableColumn<?, ?> columnsCol;
    @FXML
    private TableColumn<?, ?> maxRatsCol;
    @FXML
    private TableColumn<?, ?> timeLimitCol;

    /**
     * All the levels available to the user.
     */
    private RatGameLevel[] options;

    /**
     * Table view of level information.
     */
    @FXML
    private TableView<GameProperties> tableView;

    /**
     * The level selected, can be null.
     */
    private RatGameFile selectedLevel;

    /**
     * The players name, can be null.
     */
    private String playerName;

    /**
     * The player name entry input field.
     */
    public TextField nameTextField;

    /**
     * Loads a Level Input Form into the provided Stage and then waits until
     * the user closes the scene through the close button of the selection
     * button.
     *
     * @param s The stage to display on.
     * @return Level input form the stage loaded correctly. Otherwise, {@code
     * empty} is returned.
     */
    public static Optional<LevelInputFormController> loadAndWait(final Stage s) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Scene scene = new Scene(loader.load());
            s.setScene(scene);
            s.showAndWait();

            return Optional.of(loader.getController());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Loads a Level Input Form into the provided Stage displaying only the
     * available levels. Then waits until the user closes the scene through
     * the close button of the selection button.
     *
     * @param s The stage to display on.
     * @return Level input form the stage loaded correctly. Otherwise, {@code
     * empty} is returned.
     * @throws Exception If one occurs while reading or Parsing the levels.
     */
    public static LevelInputFormController loadAndWait(final Stage s,
                                                       final RatGameLevel... levels)
            throws Exception {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        final Scene scene = new Scene(loader.load());
        s.setScene(scene);

        final LevelInputFormController form = loader.getController();
        form.setAvailableLevels(levels);

        s.showAndWait();

        return form;
    }

    /**
     * Adds all the levels in the provided array to the Table of levels.
     *
     * @param levels The levels to add.
     */
    private void setAvailableLevels(final RatGameLevel[] levels) {
        for (RatGameLevel level : levels) {
            final GameProperties properties =
                    level.getRatGameFile().getDefaultProperties();
            this.tableView.getItems().add(properties);
        }
    }

    /**
     * @return Optional of the level selection by the player.
     */
    public Optional<RatGameFile> getLevelSelection() {
        return Optional.ofNullable(selectedLevel);
    }

    /**
     * @return Optional of the players name.
     */
    public Optional<String> getPlayerName() {
        return Optional.ofNullable(playerName);
    }

    /**
     * On level select get the selection that the user made. Or prompt them
     * with an alert if they didn't select a level.
     */
    @FXML
    private void onSelectClicked() {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            final Alert e = new Alert(Alert.AlertType.INFORMATION);
            e.setHeaderText("Please select a Level!");
            e.showAndWait();

        } else {
            final GameProperties properties =
                    tableView.getSelectionModel().getSelectedItem();
            this.selectedLevel = RatGameLevel.getLevelFromName(
                    properties.getIdentifierName()
            ).getRatGameFile();
            this.playerName = nameTextField.getText();
            tableView.getScene().getWindow().hide();
        }
    }

    /**
     * Assigns the target properties and loads up data.
     *
     * @param url            Un-used.
     * @param resourceBundle Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle resourceBundle) {
        levelNameCol.setCellValueFactory(
                new PropertyValueFactory<>("levelName")
        );
        rowCountCol.setCellValueFactory(
                new PropertyValueFactory<>("rows")
        );
        columnsCol.setCellValueFactory(
                new PropertyValueFactory<>("columns")
        );
        maxRatsCol.setCellValueFactory(
                new PropertyValueFactory<>("maxRats")
        );
        timeLimitCol.setCellValueFactory(
                new PropertyValueFactory<>("timeLimit")
        );
    }

    /**
     *
     * @param keyEvent
     */
    public void onKeyTypedTextField(final KeyEvent keyEvent) {

    }
}
