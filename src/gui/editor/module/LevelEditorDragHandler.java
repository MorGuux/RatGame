package gui.editor.module;

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
     */
    void handle(final LevelEditor editor, final DragEvent event);
}
