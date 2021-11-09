package gui.itemview;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Objects;

/**
 * Item View Scene which displays visually a game item and its generator state.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class ItemViewController {

    public static final URL SCENE_FXML = ItemViewController.class.getResource("ItemScene.fxml");

    private static final String USAGE_STR = "Usages: ";

    @FXML
    private BorderPane mainPane;
    @FXML
    private ImageView itemImage;
    @FXML
    private Label usagesLabel;

    public void setStyleSheet(final String styleSheet) {
        Objects.requireNonNull(styleSheet);
        mainPane.getStylesheets().clear();
        mainPane.getStylesheets().add(styleSheet);
    }

    public void setItemImage(final Image img) {
        Objects.requireNonNull(img);
        this.itemImage.setImage(img);
    }

    public void setUsagesValue(final int value) {
        this.usagesLabel.setText(USAGE_STR + value);
    }
}
