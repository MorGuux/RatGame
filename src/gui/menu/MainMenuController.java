package gui.menu;


import game.motd.MOTDClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import launcher.Main;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller
     * so that it can be accessed.
     */
    public static URL SCENE_FXML =
            MainMenuController.class.getResource("MainMenu.fxml");

    /**
     * Message of the day client; our webhook to CS-Webcat.
     */
    private MOTDClient client;

    /**
     * Timer that constantly checks for new messages.
     */
    private Timer motdPinger;

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
     * @param url Un-used.
     * @param unused Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle unused) {
        client = new MOTDClient();

        // Periodically check every 5 seconds
        final int delay = 5_000;
        motdPinger = new Timer(delay, (e) -> {
            if (client.hasNewMessage()) {
                try {
                    handleNewMessage(client.getMessage());
                } catch (IOException
                        | InterruptedException ignored) {
                    // https://i.imgflip.com/26h3xi.jpg
                }
            }
        });

        // Initial delay (start immediately, on '#start')
        motdPinger.setInitialDelay(0);
        motdPinger.start();
    }

    /**
     * Updates the Message of the Day label to a new message.
     *
     * @param msg The new message of the day.
     */
    private void handleNewMessage(final String msg) {
        Platform.runLater(() -> motdLabel.setText(msg));
    }

    /**
     * Disables the Message of the Day tracker.
     */
    private void stopMotdTracker() {
        this.motdPinger.stop();
    }

    /**
     * Initialises a brand new RatGame. You will need to gather the
     * following:
     * <ol>
     *     <li>The Rat Game Level (Default)</li>
     *     <li>Player Profile</li>
     * </ol>
     *
     */
    public void onStartGameClicked() throws IOException {
        //todo Temporary, access to the game scene
        final FXMLLoader loader = Main.loadGameStage();
        final Scene sc = new Scene(loader.load());
        Main.loadNewScene(sc);
    }

    /**
     *
     */
    public void onLoadGameClicked() {
        // todo
    }

    /**
     *
     */
    public void onAboutClicked() {
        //todo
    }

    /**
     *
     */
    public void onChangeStyleClicked() {
        //todo
    }
}
