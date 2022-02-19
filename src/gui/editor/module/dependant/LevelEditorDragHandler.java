package gui.editor.module.dependant;

import gui.editor.LevelEditor;
import javafx.scene.input.DragEvent;

/**
 * Java interface created on 11/02/2022 for usage in project RatGame-A2.
 * Event redirect so that each module can handle the result of their drag drop.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface LevelEditorDragHandler {

    /**
     * Handles a drag event that occurred in a level editor.
     *
     * @param editor The editor that detected the drag event.
     * @param event  The event that occurred.
     * @param row    The row position in the grid map of this events final
     *               mouse position.
     * @param col    The col position in the grid map of this events final mouse
     *               position.
     */
    void handle(LevelEditor editor, DragEvent event, int row, int col);
}
