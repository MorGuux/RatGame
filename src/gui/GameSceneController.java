package gui;

import javafx.fxml.FXML;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import java.net.URL;

public class GameSceneController {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller so that it can be accessed.
     */
    public static URL SCENE_FXML = GameSceneController.class.getResource("GameScene.fxml");

    @FXML
    private BorderPane window;

    @FXML
    private BorderPane gameWindow;

    @FXML
    private BorderPane item;

    public void itemDragDetected(MouseEvent event) {
        // Mark the drag as started.
        // We do not use the transfer mode (this can be used to indicate different forms
        // of drags operations, for example, moving files or copying files).
        Dragboard db = item.startDragAndDrop(TransferMode.ANY);

        // We have to put some content in the clipboard of the drag event.
        // We do not use this, but we could use it to store extra data if we wished.
        ClipboardContent content = new ClipboardContent();
        content.putString("Hello");
        db.setContent(content);

        // Consume the event. This means we mark it as dealt with.
        event.consume();
    }

    public void gameWindowDragOver(DragEvent event) {
        // Mark the drag as acceptable if the source was the draggable image.
        // (for example, we don't want to allow the user to drag things or files into our application)
        if (event.getGestureSource() == item) {
            // Mark the drag event as acceptable by the canvas.
            event.acceptTransferModes(TransferMode.ANY);
            // Consume the event. This means we mark it as dealt with.
            event.consume();
        }
    }

    public void gameWindowDragDropped(DragEvent event) {
        // We call this method which is where the bulk of the behaviour takes place.
        itemPlaced(event);
        // Consume the event. This means we mark it as dealt with.
        event.consume();
    }

    // This method is called when the window receives a dragged object.
    private void itemPlaced(DragEvent event) {
        double x = event.getX();
        double y = event.getY();

        //TODO: Add code to place the item in the game window.
    }

}
