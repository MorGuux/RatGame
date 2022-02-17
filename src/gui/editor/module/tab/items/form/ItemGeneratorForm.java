package gui.editor.module.tab.items.form;

import game.generator.ItemGenerator;
import game.generator.loader.ItemGeneratorLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import util.SceneUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Java class created on 17/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class ItemGeneratorForm implements Initializable {

    private static final URL SCENE_FXML
            = ItemGeneratorForm.class.getResource("ItemGeneratorForm.fxml");

    ///////////////////////////////////////////////////////////////////////////
    // FXML Attributes
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private ChoiceBox<ItemGeneratorLoader.GeneratorTarget> itemClassChoiceBox;
    @FXML
    private TextField curUsagesText;
    @FXML
    private TextField maxUsagesText;
    @FXML
    private TextField curRefreshTimeText;
    @FXML
    private TextField maxRefreshTimeText;

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    private Parent root;
    private Stage displayStage;
    private boolean isNaturalExit = false;

    ///////////////////////////////////////////////////////////////////////////
    // Static construction mechanisms
    ///////////////////////////////////////////////////////////////////////////

    public static ItemGeneratorForm init(final Stage s) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final ItemGeneratorForm form = loader.getController();

            form.root = root;
            form.displayStage = s;
            s.setScene(new Scene(root));

            return form;

            // Rethrow
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handlers
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private void onBuildGeneratorClicked() {
        if (this.isComplete()) {
            this.isNaturalExit = true;
            this.displayStage.close();

            // Error message
        } else {
            final Alert ae = new Alert(Alert.AlertType.WARNING);
            ae.setHeaderText("Form Incomplete!!!");
            ae.setContentText("Some form data has not been supplied and is "
                    + "thus not able to compile.");
            ae.showAndWait();
        }
    }

    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {
        for (ItemGeneratorLoader.GeneratorTarget v :
                ItemGeneratorLoader.GeneratorTarget.values()) {
            this.itemClassChoiceBox.getItems().add(v);
        }

        final Supplier<TextFormatter<?>> v = () -> {
            return new TextFormatter<>(SceneUtil.POSITIVE_INTEGER_FORMAT);
        };

        // Text formatters only allow 1 -> INT_MAX - 1
        this.curRefreshTimeText.setTextFormatter(v.get());
        this.curUsagesText.setTextFormatter(v.get());
        this.maxRefreshTimeText.setTextFormatter(v.get());
        this.maxUsagesText.setTextFormatter(v.get());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data collection methods (get/set)
    ///////////////////////////////////////////////////////////////////////////

    public boolean isComplete() {
        // Check data validity
        final Function<TextField, Boolean> fn = (t) -> !t.getText().equals("");
        return itemClassChoiceBox.getValue() != null
                && fn.apply(curUsagesText)
                && fn.apply(curRefreshTimeText)
                && fn.apply(maxUsagesText)
                && fn.apply(maxRefreshTimeText);
    }

    public Parent getRoot() {
        return root;
    }

    public Stage getDisplayStage() {
        return displayStage;
    }

    public boolean isNaturalExit() {
        return isNaturalExit;
    }

    public Optional<ItemGenerator<?>> createGenerator() {

        final Function<TextField, Integer> fn
                = (t) -> Integer.parseInt(t.getText());

        if (isComplete()) {
            try {
                final ItemGenerator<?> gen = this.itemClassChoiceBox
                        .getSelectionModel()
                        .getSelectedItem()
                        .create(
                                fn.apply(this.maxRefreshTimeText),
                                fn.apply(this.curRefreshTimeText),
                                fn.apply(this.curUsagesText),
                                fn.apply(this.maxUsagesText)
                        );

                return Optional.of(gen);

                // Print stack trace since we want to know if this happens
            } catch (final Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }

        } else {
            return Optional.empty();
        }
    }

    public void clear() {
        this.isNaturalExit = false;
        this.maxRefreshTimeText.clear();
        this.curUsagesText.clear();
        this.maxUsagesText.clear();
        this.curRefreshTimeText.clear();
    }
}
