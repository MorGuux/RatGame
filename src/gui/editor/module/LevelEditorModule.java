package gui.editor.module;

import gui.editor.LevelEditor;

/**
 * Java interface created on 11/02/2022 for usage in project RatGame-A2.
 * Interface which all Level Editor modules will implement.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public interface LevelEditorModule {

    /**
     * Loads the module into the level editor scene. So that it can be
     * interacted with.
     * <p>
     * Note that you should store the provided {@link LevelEditor} locally in
     * your class then use that when interacting with something in the editor.
     *
     * @param editor The editor to load into.
     */
    void loadIntoScene(final LevelEditor editor);
}
