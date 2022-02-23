package gui.editor;

import game.contextmap.ContextualMap;
import game.entity.Entity;
import game.generator.ItemGenerator;
import game.level.levels.template.TemplateEditor;
import game.level.levels.template.TemplateElement;
import game.tile.Tile;
import gui.editor.module.grid.entityview.EntityViewModule;
import gui.editor.module.grid.tileview.TileViewModule;
import gui.editor.module.tab.TabModules;
import gui.editor.module.tab.properties.PropertiesTab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Java class created on 22/02/2022 for usage in project RatGame-A2. Used to
 * abstract the process of data collection and writing for level editor
 * information.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public class LevelEditorWriteContext {

    /**
     * The number of spaces to apply to any string which needs to be indented.
     */
    private static final byte INDENT_SPACE_COUNT = 4;

    /**
     * The level editor used for writing to a file.
     */
    private final LevelEditor editor;

    /**
     * Convenience attribute accessing the TileViewModule of the level editor.
     */
    private final TileViewModule tileView;

    /**
     * Convenience attribute accessing the EntityViewModule of the level editor.
     */
    private final EntityViewModule entityView;

    /**
     * Convenience attribute accessing the Tab section of the level editor.
     */
    private final TabModules tabs;

    /**
     * Indents the provided string using the default indent space count.
     *
     * @param s The string to indent.
     * @return Indented string.
     */
    private static String indent(final String s) {
        return String.format(
                "%s%s",
                " ".repeat(INDENT_SPACE_COUNT),
                s
        );
    }

    /**
     * Constructs the write context using the editor is its target for data
     * collection.
     *
     * @param editor The editor to save to a file.
     * @throws NullPointerException If the editor is null.
     */
    public LevelEditorWriteContext(final LevelEditor editor) {
        this.editor = Objects.requireNonNull(editor);
        this.tileView = editor.getTileViewModule();
        this.entityView = editor.getEntityViewModule();
        this.tabs = editor.getTabModules();
    }

    /**
     * Loads all the required Level data and then saves it to the target file.
     *
     * @param target The file to save the content to.
     * @throws IOException If the provided file could not be written to; The
     *                     template writer failed to load; or if any other
     *                     IOExceptions occur before the final write.
     */
    public void saveInfoToTarget(final File target) throws IOException {

        // Ensure file exists and can be written to
        if (target == null || !target.isFile()) {
            throw new FileNotFoundException(
                    "Target file is not an acceptable file: "
                            + target
            );
        }

        final TemplateEditor editor = new TemplateEditor();

        // Collect and write data
        loadPropertiesToTarget(editor);
        loadItemGeneratorsToTarget(editor);
        loadTilesToTarget(editor);
        loadEntitiesToTarget(editor);

        // Write to file
        editor.writeContentToFile(target);
    }

    /**
     * Loads the properties elements into the target editor.
     *
     * @param editor The editor
     */
    private void loadPropertiesToTarget(final TemplateEditor editor) {
        final PropertiesTab tab = this.tabs.getPropertiesTab();

        editor.setElement(
                TemplateElement.FRIENDLY_NAME,
                tab.getLevelName()
        );
        editor.setElement(
                TemplateElement.MAP_ROW_COUNT,
                String.valueOf(this.editor.getRows())
        );
        editor.setElement(
                TemplateElement.MAP_COL_COUNT,
                String.valueOf(this.editor.getCols())
        );
        editor.setElement(
                TemplateElement.TIME_LIMIT,
                String.valueOf(tab.getTimeLimit())
        );
        editor.setElement(
                TemplateElement.MAX_RATS,
                String.valueOf(tab.getMaxRats())
        );
    }

    /**
     * Loads the item generators into the target editor.
     *
     * @param editor The editor to load the item generators into.
     * @see TemplateElement#ITEM_GENERATOR
     */
    private void loadItemGeneratorsToTarget(final TemplateEditor editor) {
        final StringJoiner sj = new StringJoiner(System.lineSeparator());

        final ItemGenerator<?>[] generators
                = tabs.getItemGeneratorTab().getGenerators();
        for (final ItemGenerator<?> gen : generators) {
            sj.add(indent(gen.buildToString()));
        }

        editor.setElement(TemplateElement.ITEM_GENERATOR, sj.toString());
    }

    /**
     * Loads the game tile map to the provided editor.
     *
     * @param editor The editor to load tiles into.
     * @see TemplateElement#MAP_LAYOUT
     */
    private void loadTilesToTarget(final TemplateEditor editor) {
        final Tile[][] tileGrid = this.tileView.getTileMapRaw();

        final StringJoiner sj = new StringJoiner(System.lineSeparator());
        for (final Tile[] tiles : tileGrid) {
            for (final Tile tile : tiles) {
                sj.add(indent(tile.buildToString()));
            }
        }

        editor.setElement(TemplateElement.MAP_LAYOUT, sj.toString());
    }

    /**
     * Loads all entities to the provided editor.
     *
     * @param editor The editor load into.
     * @see TemplateElement#ENTITY_INSTANCES
     */
    private void loadEntitiesToTarget(final TemplateEditor editor) {
        final Entity[] entities = entityView.getAllEntities();
        final StringJoiner sj = new StringJoiner(System.lineSeparator());
        final ContextualMap tempMap = ContextualMap.emptyMap(
                this.editor.getRows(),
                this.editor.getCols()
        );

        for (final Entity e : entities) {
            tempMap.placeIntoGame(e);
            sj.add(indent(e.buildToString(tempMap)));
        }

        editor.setElement(TemplateElement.ENTITY_INSTANCES, sj.toString());
    }
}
