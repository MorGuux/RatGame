package gui.editor.module.dependant;

import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

/**
 * Java interface created on 20/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public interface LevelEditorMouseHandler {

    /**
     * Handles any and all mouse event types that can occur, such as Mouse
     * Clicked or Mouse Dragged.
     * <p>
     * Note: That you should not attempt to remove the listener or handler
     * from within the handle call.
     *
     * @param type  The actual event type such as Drag, or Click.
     * @param event The event data.
     * @param row   The corresponding Row position in the display grid.
     * @param col   The corresponding Col position in the display grid.
     */
    void handle(EventType<MouseEvent> type,
                MouseEvent event,
                int row,
                int col);
}
