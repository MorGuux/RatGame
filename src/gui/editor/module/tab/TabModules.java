package gui.editor.module.tab;

import gui.editor.LevelEditor;
import gui.editor.module.dependant.LevelEditorModule;
import gui.editor.module.tab.entities.EntitiesTab;
import gui.editor.module.tab.properties.PropertiesTab;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2. Class
 * consists of all the Tabs that the editor will need.
 *
 * @author -Ry
 * @version 0.3
 * Copyright: N/A
 */
public class TabModules implements LevelEditorModule {

    /**
     * The level editor that this module is being displayed in.
     */
    private LevelEditor editor;

    /**
     * The properties tab controller.
     */
    private final PropertiesTab propertiesTab;

    /**
     * The entities tab controller.
     */
    private final EntitiesTab entitiesTab;

    /**
     * Constructs the tab modules.
     */
    public TabModules() {
        this.propertiesTab = PropertiesTab.init(this);
        this.entitiesTab = EntitiesTab.init(this);
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

        // Properties
        propertiesTab.setOriginalProperties(
                editor.getFileToEdit().getDefaultProperties()
        );
        propertiesTab.resetToDefaults();
        editor.getGeneralTabBorderpane().setCenter(propertiesTab.getRoot());

        // Entities
        editor.getEntitiesTabBorderpane().setCenter(entitiesTab.getRoot());
        editor.getFileToEdit().getEntityPositionMap().forEach((e, pos) -> {
            entitiesTab.addExistingEntity(e);
        });
    }

    /**
     * @return The level editor that this is a module of.
     */
    public LevelEditor getEditor() {
        return editor;
    }

    /**
     * @return The entities tab.
     */
    public EntitiesTab getEntitiesTab() {
        return entitiesTab;
    }

    /**
     * @return The level properties tab.
     */
    public PropertiesTab getPropertiesTab() {
        return propertiesTab;
    }
}
