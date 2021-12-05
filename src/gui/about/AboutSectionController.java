package gui.about;

import gui.leaderboard.LeaderboardPlayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * Controller for the leaderboard accessed from the main menu. Includes a
 * level selection and a sorted table of the top 10 players.
 *
 * @author Maksim Samokhvalov
 * @version 0.1
 * Copyright: N/A
 */
public class AboutSectionController {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller
     * so that it can be accessed.
     */
    public static final URL SCENE_FXML =
            gui.about.AboutSectionController.class.getResource(
                    "AboutSection" +
                    ".fxml");

    /**
     * Pane which sits behind the main menu buttons.
     */
    @FXML
    private Pane backgroundPane;

    /**
     * Message of the day label.
     */
    @FXML
    private Label motdLabel;


}