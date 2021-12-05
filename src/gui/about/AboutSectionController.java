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
public class AboutSectionController implements Initializable {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller
     * so that it can be accessed.
     */
    public static final URL SCENE_FXML =
            gui.about.AboutSectionController.class.getResource(
                    "AboutSection" +
                    ".fxml");

    /**
     * The time in milliseconds that the MOTD Client will be checked by the
     * motdPinger for new messages.
     */
    private static final int UPDATE_RATE = 5000;

    /**
     * Message of the day client; our webhook to CS-Webcat.
     */
    private MOTDClient client;

    /**
     * Timer that constantly checks for new messages.
     */
    private Timer motdPinger;

    /**
     * A list of the motd pingers that will be notified every 5 seconds about
     * a message of the day. Synchronised so that we don't have to stop the
     * motdPinger in order to register a new pinger.
     */
    private final List<Consumer<String>> motdPingers
            = Collections.synchronizedList(new ArrayList<>());

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
     * Setup MOTD pinger to constantly update the new
     * message of the day.
     *
     * @param url    Un-used.
     * @param unused Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle unused) {

        // Creating message of the day client
        client = new MOTDClient();
        motdPinger = new Timer();
        motdPingers.add((s) -> this.motdLabel.setText(s));
        motdPinger.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String msg = client.getMessage();
                if (client.hasNewMessage()) {
                    msg = client.getMessage();
                }

                // Looks a bit weird, but we always want to set the string on
                // an update.
                final String actual = msg;
                for (Consumer<String> pinger : motdPingers) {
                    Platform.runLater(() -> {
                        pinger.accept(actual);
                    });
                }

            }
        }, 0, UPDATE_RATE);


        // Shutdown the timer task when the scene is closed (has to be
        // initialised after load)
        Platform.runLater(() -> {
            this.backgroundPane.getScene().getWindow().setOnCloseRequest(
                    (e) -> this.motdPinger.cancel()
            );
        });
    }

}