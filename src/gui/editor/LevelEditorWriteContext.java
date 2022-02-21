package gui.editor;

import game.contextmap.ContextualMap;
import game.entity.Entity;
import game.generator.ItemGenerator;
import game.level.levels.template.TemplateEditor;
import game.level.levels.template.TemplateElement;
import gui.editor.module.grid.entityview.EntityViewModule;
import gui.editor.module.grid.tileview.TileViewModule;
import gui.editor.module.tab.TabModules;
import gui.editor.module.tab.items.ItemViewTab;
import gui.editor.module.tab.properties.PropertiesTab;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.StringJoiner;

/**
 * Java class created on 21/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class LevelEditorWriteContext {

    private static final URL SCENE_FXML
            = LevelEditorWriteContext.class.getResource(
            "LevelEditorWriteContext.fxml");

    @FXML
    private ProgressBar totalTaskProgressBar;
    @FXML
    private Label curTaskInfoLabel;
    @FXML
    private Label currentTaskNumLabel;
    @FXML
    private Label currentTaskLabel;

    private Parent root;
    private Stage displayStage;
    private LevelEditor editor;

    public static LevelEditorWriteContext init(final Stage displayStage,
                                               final LevelEditor editor) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final LevelEditorWriteContext context = loader.getController();

            context.root = root;
            context.displayStage = displayStage;
            context.editor = editor;

            return context;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public LevelEditor getEditor() {
        return editor;
    }

    public Stage getDisplayStage() {
        return displayStage;
    }

    public void start(final File target) throws Exception {

        final TemplateEditor writtenContent = new TemplateEditor();
        this.displayStage.setScene(new Scene(this.root));
        this.displayStage.show();
        final TabModules tabs = editor.getTabModules();

        // Init properties
        this.currentTaskLabel.setText("Writing Level Properties!");
        this.loadProperties(writtenContent, editor, tabs.getPropertiesTab());

        // Init item generators
        this.currentTaskLabel.setText("Loading Item Generators!");
        this.loadItemGenerators(
                writtenContent,
                editor,
                tabs.getItemGeneratorTab()
        );

        // Init Tiles
        this.curTaskInfoLabel.setText("Loading Tiles to level!");
        this.loadTiles(writtenContent, editor, editor.getTileViewModule());

        // Init Entities
        this.curTaskInfoLabel.setText("Loading Entities to level!");
        this.loadEntities(writtenContent, editor, editor.getEntityViewModule());

        this.currentTaskLabel.setText("Finalising collected data!");
        this.curTaskInfoLabel.setText(
                "Saving data to file: "
                        + target.getAbsolutePath()
        );
        System.out.println(writtenContent.getModifiedContent());
        this.displayStage.close();
    }

    private void loadProperties(final TemplateEditor writer,
                                final LevelEditor editor,
                                final PropertiesTab tab) {

        this.curTaskInfoLabel.setText("Writing Max Rats...");
        writer.setElement(
                TemplateElement.MAX_RATS,
                String.valueOf(tab.getMaxRats())
        );

        this.curTaskInfoLabel.setText("Writing Time Limit...");
        writer.setElement(
                TemplateElement.TIME_LIMIT,
                String.valueOf(tab.getTimeLimit())
        );

        this.curTaskInfoLabel.setText("Writing Level Friendly Name...");
        writer.setElement(
                TemplateElement.FRIENDLY_NAME,
                tab.getLevelName()
        );

        this.curTaskInfoLabel.setText("Writing Map Row Count...");
        writer.setElement(
                TemplateElement.MAP_ROW_COUNT,
                String.valueOf(editor.getRows())
        );

        this.curTaskInfoLabel.setText("Writing Map Col Count...");
        writer.setElement(
                TemplateElement.MAP_COL_COUNT,
                String.valueOf(editor.getCols())
        );
    }

    private void loadItemGenerators(final TemplateEditor writer,
                                    final LevelEditor editor,
                                    final ItemViewTab view) {
        this.curTaskInfoLabel.setText("Gathering item generators...");
        final ItemGenerator<?>[] generators = view.getGenerators();

        final StringJoiner generatorInfo
                = new StringJoiner(System.lineSeparator());
        for (final ItemGenerator<?> generator : generators) {
            final String str = generator.buildToString();

            this.curTaskInfoLabel.setText("Generator: " + str);
            generatorInfo.add(str);
        }

        this.curTaskInfoLabel.setText("Committing Item Generators...");
        writer.setElement(
                TemplateElement.ITEM_GENERATOR,
                generatorInfo.toString()
        );
    }

    private void loadTiles(final TemplateEditor writer,
                           final LevelEditor editor,
                           final TileViewModule view) {

        final StringJoiner tiles = new StringJoiner(System.lineSeparator());

        // Iterate all tiles and load them up
        final String base = "Loading Tile: (%s, %s)...";
        for (int row = 0; row < editor.getRows(); ++row) {
            for (int col = 0; col < editor.getCols(); ++col) {

                this.curTaskInfoLabel.setText(String.format(base, row, col));
                tiles.add(view.getTileAt(row, col).buildToString());
            }
        }

        // Commit tile info to the writer
        this.currentTaskLabel.setText("Committing Tile Data!");
        writer.setElement(TemplateElement.MAP_LAYOUT, tiles.toString());
    }

    private void loadEntities(final TemplateEditor writer,
                              final LevelEditor editor,
                              final EntityViewModule view) {

        this.curTaskInfoLabel.setText("Gathering dependencies...");
        final ContextualMap map = ContextualMap.emptyMap(
                editor.getRows() + 1,
                editor.getCols() + 1
        );

        final Entity[] entities = view.getAllEntities();
        final StringJoiner entityInfo
                = new StringJoiner(System.lineSeparator());
        for (final Entity e : entities) {
            this.curTaskInfoLabel.setText("Loading entity...");
            map.placeIntoGame(e);
            final String entity = e.buildToString(map);

            this.curTaskInfoLabel.setText("Entity: " + entity);
            entityInfo.add(entity);
        }

        this.currentTaskLabel.setText("Committing Entity Data...");
        writer.setElement(
                TemplateElement.ENTITY_INSTANCES,
                entityInfo.toString()
        );
    }
}
