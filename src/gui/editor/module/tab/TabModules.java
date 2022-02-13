package gui.editor.module.tab;

import gui.editor.LevelEditor;
import gui.editor.module.dependant.LevelEditorModule;
import gui.editor.module.tab.properties.PropertiesTab;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2. Class
 * consists of all the Tabs that the editor will need.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class TabModules implements LevelEditorModule {

    private LevelEditor editor;
    private final PropertiesTab propertiesTab;

    public TabModules() {
        this.propertiesTab = PropertiesTab.init();
    }

    /**
     * Loads the module into the level editor scene. So that it can be
     * interacted with.
     * <p>
     * Note that you should store the provided {@link LevelEditor} locally in
     * your class then use that when interacting with something in the editor.
     *
     * @param editor The editor to load into.
     */
    @Override
    public void loadIntoScene(final LevelEditor editor) {
        this.editor = editor;
        propertiesTab.setOriginalProperties(
                editor.getFileToEdit().getDefaultProperties()
        );
        propertiesTab.resetToDefaults();

        editor.getGeneralTabBorderpane().setCenter(propertiesTab.getRoot());
    }
}
