package gui.editor.module.tab.items;

import game.generator.ItemGenerator;
import gui.editor.LevelEditor;
import gui.editor.module.dependant.LevelEditorModule;
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private final List<ItemGeneratorEditor> itemGenerators
            = Collections.synchronizedList(new ArrayList<>());

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
                    this.itemGenerators.add(e);
                    this.contentContainerVBox.getChildren().add(e.getRoot());
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

            opt.ifPresent((s) -> System.out.println(s.buildToString()));
        }
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
}
