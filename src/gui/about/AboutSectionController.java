package gui.about;


import game.motd.MOTDClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

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
                    "AboutSection" + ".fxml");

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

    /**
     * Set the message of the day label for the scene.
     *
     * @param s The new message of the day.
     */
    public void setMotdLabel(final String s) {
        this.motdLabel.setText(s);
    }
}
