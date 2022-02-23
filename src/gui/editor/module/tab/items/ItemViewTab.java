package gui.editor.module.tab.items;

import game.generator.ItemGenerator;
import gui.editor.LevelEditor;
import gui.editor.module.tab.TabModuleContent;
import gui.editor.module.tab.TabModules;
import gui.editor.module.tab.items.form.ItemGeneratorForm;
import gui.editor.module.tab.items.view.ItemGeneratorEditor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.SceneUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Java class created on 17/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class ItemViewTab implements TabModuleContent {

    private static final URL SCENE_FXML
            = ItemViewTab.class.getResource("ItemViewTab.fxml");

    ///////////////////////////////////////////////////////////////////////////
    // FXML Attributes
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private VBox contentContainerVBox;

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    private LevelEditor editor;
    private Parent root;
    private ItemGeneratorForm latestForm;
    private final Set<ItemGeneratorEditor> itemGenerators = new HashSet<>();

    ///////////////////////////////////////////////////////////////////////////
    // Static construction mechanisms
    ///////////////////////////////////////////////////////////////////////////

    public static ItemViewTab init() {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final ItemViewTab tab = loader.getController();

            tab.root = root;

            final Stage s = new Stage();
            s.initModality(Modality.APPLICATION_MODAL);
            tab.latestForm = ItemGeneratorForm.init(s);

            return tab;
            // Rethrow
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handles
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Called when this tab is loaded by the container module, when said
     * container module is in a state to do so.
     *
     * @param editor    The editor that the module is a part of.
     * @param container The literal container tab of this content.
     */
    @Override
    public void loadIntoScene(final LevelEditor editor,
                              final TabModules container) {
        this.editor = editor;
        this.editor.getItemPoolTabBorderpane().setCenter(this.root);

        this.editor.getFileToEdit()
                .getDefaultGenerator()
                .getGenerators()
                .forEach(i -> {
                    final ItemGeneratorEditor e = ItemGeneratorEditor.init(i);
                    e.setOnDeleteActionHandle(this::deleteItemView);
                    this.contentContainerVBox.getChildren().add(e.getRoot());
                    this.itemGenerators.add(e);
                });
    }

    @FXML
    private void onCreateNewClicked() {
        if (this.latestForm.isNaturalExit()) {
            this.latestForm.clear();
        }

        this.latestForm.getDisplayStage().showAndWait();

        if (this.latestForm.isComplete()) {
            final Optional<ItemGenerator<?>> opt
                    = this.latestForm.createGenerator();

            opt.ifPresent((s) -> {
                final ItemGeneratorEditor ed = ItemGeneratorEditor.init(s);
                ed.setOnDeleteActionHandle(this::deleteItemView);
                SceneUtil.scaleNodeIn(ed.getRoot());
                this.contentContainerVBox.getChildren().add(ed.getRoot());
                this.itemGenerators.add(ed);
            });
        }
    }

    private void deleteItemView(final ItemGeneratorEditor editor) {
        this.contentContainerVBox.getChildren().remove(editor.getRoot());
        this.itemGenerators.remove(editor);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data collection methods
    ///////////////////////////////////////////////////////////////////////////

    public Parent getRoot() {
        return root;
    }

    public LevelEditor getEditor() {
        return editor;
    }

    public ItemGenerator<?>[] getGenerators() {
        final List<ItemGenerator<?>> generatorList = new ArrayList<>();
        this.itemGenerators.forEach((i) -> generatorList.add(i.getGenerator()));
        return generatorList.toArray(new ItemGenerator[0]);
    }
}
