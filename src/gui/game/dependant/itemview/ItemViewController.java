package gui.game.dependant.itemview;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Item View Scene which displays visually a game item and its generator state.
 *
 * @author -Ry
 * @version 0.3
 * Copyright: N/A
 */
public class ItemViewController {

    //todo program drag and drop feature

    /**
     * Scene FXML Resource location.
     */
    private static final URL SCENE_FXML
            = ItemViewController.class.getResource("ItemScene.fxml");

    /**
     * Usages template string.
     */
    private static final String USAGES_STR = "%s/%s";

    /**
     * Progress bar that specifies how close we are to a new usage for the item.
     */
    @FXML
    private ProgressBar itemRefreshProgress;

    /**
     * Name of this item.
     */
    @FXML
    private Label itemNameLabel;

    /**
     * Contains smaller all the Image views representing graphically the
     * number of usages this item has.
     */
    @FXML
    private HBox subItemView;

    /**
     * Main container pane that wraps all nodes for this hierarchy.
     */
    @FXML
    private BorderPane mainPane;

    /**
     * Label which displays a number for the number of usages an Item has.
     * This label should be of the format: 'X/Y' where, 'x' is the current
     * available number of usages. And 'y' is the maximum ever number of usages.
     */
    @FXML
    private Label usageCountLabel;

    /**
     * Main Item image view contains a bigger image of what this item is.
     */
    @FXML
    private ImageView mainItemImageView;

    /**
     * Current number of usages for this item.
     */
    private int currentUsages = 0;

    /**
     * Maximum number of usages for this item.
     */
    private int maxUsages = 0;

    /**
     * Loads an empty Item View scene.
     *
     * @return Newly initiated item view scene.
     */
    public static ItemViewController loadView() {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);
        try {
            loader.load();

            return loader.getController();

            // We eat the exception though still re-throw
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * @return Container of the entire object hierarchy for this scene.
     */
    public Parent getRoot() {
        return this.mainPane;
    }

    /**
     * Set the Item image to the provided image.
     *
     * @param img The image to display. Note that this image is forced to fit
     *            into 32x32.
     */
    public void setItemImage(final Image img) {
        Objects.requireNonNull(img);
        this.mainItemImageView.setImage(img);
    }

    /**
     * Updates the Item name label to the provided name.
     *
     * @param name The name of this item.
     */
    public void setItemName(final String name) {
        Objects.requireNonNull(name);
        this.itemNameLabel.setText(name);
    }

    /**
     * Set the current number of usages to the provided number.
     *
     * @param cur The new current number of usages.
     */
    public void setCurrentUsages(final int cur) {
        this.currentUsages = cur;
        updateUsages();
        updateItemVisuals();
    }

    /**
     * @return Current number of usages this item has.
     */
    public int getCurrentUsages() {
        return currentUsages;
    }

    /**
     * Set the maximum number of usages to the provided number.
     *
     * @param max The new maximum number of usages.
     */
    public void setMaxUsages(final int max) {
        this.maxUsages = max;
        updateUsages();
    }

    /**
     * @return Maximum number of usages this item should ever have.
     */
    public int getMaxUsages() {
        return maxUsages;
    }

    /**
     * Updates current usages label to display visually. Does so regardless
     * of if the values have changed or not.
     */
    private void updateUsages() {
        this.usageCountLabel.setText(String.format(
                USAGES_STR,
                currentUsages,
                maxUsages
        ));
    }

    /**
     *
     * @param progress
     */
    public void setCurrentProgress(final double progress) {
        this.itemRefreshProgress.setProgress(progress);
    }

    /**
     * Updates visually the display data for the Item itself. Either by
     * removing or adding some images which represent the items.
     */
    private void updateItemVisuals() {
        final int total = subItemView.getChildren().size();
        if (total > currentUsages) {
            removeItemView(total);
        } else {
            final Image img = this.mainItemImageView.getImage();
            final int size = (int) (this.mainItemImageView.getFitHeight() / 2);
            addItemView(img, size);
        }
    }

    /**
     * Removes n item amounts until we finally are equal to the provided total.
     *
     * @param total Number of Images views to keep.
     */
    private void removeItemView(final int total) {
        int count = total;
        while (count > currentUsages) {
            count--;

            final int index = subItemView.getChildren().size() - 1;
            subItemView.getChildren().remove(index);
        }
    }

    /**
     * Adds image views to the image view section representing the number of
     * current usages.
     *
     * @param img  The image to display.
     * @param size The size to force the image to.
     */
    private void addItemView(final Image img,
                             final int size) {
        int count = subItemView.getChildren().size();
        while (count < currentUsages) {
            count++;

            subItemView.getChildren().add(supplyView(img, size));
        }
    }

    /**
     * Supplies an image view of the provided image to the provided size.
     *
     * @param img  The image to display.
     * @param size The size of the View (Square sxs).
     * @return Newly constructed image view.
     */
    private ImageView supplyView(final Image img,
                                 final int size) {
        final ImageView view = new ImageView(img);
        view.setFitHeight(size);
        view.setFitWidth(size);
        view.setPreserveRatio(false);
        view.setSmooth(false);
        return view;
    }

    /**
     * Update the stylesheet for this node hierarchy to the provided stylesheet.
     *
     * @param stylesheet The new style to use.
     */
    public void setStylesheet(final String stylesheet) {
        mainPane.getStylesheets().clear();
        mainPane.getStylesheets().add(stylesheet);
    }
}
