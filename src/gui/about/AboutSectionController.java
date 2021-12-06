package gui.about;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.net.URL;

/**
 * Controller for the About section accessed through the main menu. Contains
 * acknowledgements and tribute to developers.
 *
 * @author Maksim Samokhvalov
 * @version 0.3
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
