package gui.editor.module.tab;

import gui.editor.LevelEditor;

/**
 * Java interface created on 15/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public interface TabModuleContent {

    /**
     * Called when this tab is loaded by the container module, when said
     * container module is in a state to do so.
     *
     * @param editor    The editor that the module is a part of.
     * @param container The literal container tab of this content.
     */
    void loadIntoScene(LevelEditor editor, TabModules container);
}
