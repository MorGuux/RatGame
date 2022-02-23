package gui.type;

import game.classinfo.field.EnumerableValue;
import game.classinfo.field.Type;
import game.classinfo.field.TypeInstantiationException;
import game.classinfo.tags.WritableField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import util.SceneUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java class created on 18/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class TypeConstructionForm {

    ///////////////////////////////////////////////////////////////////////////
    // FXML Attributes
    ///////////////////////////////////////////////////////////////////////////

    private static final URL SCENE_FXML
            = TypeConstructionForm.class.getResource(
            "TypeConstructionForm.fxml");

    @FXML
    private VBox typeChildFormVBox;

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    private final Map<Node, Pair<Field, Type>> nodeFieldTypeMap
            = new HashMap<>();
    private Parent root;
    private Stage displayStage;
    private boolean isNaturalExit = false;

    ///////////////////////////////////////////////////////////////////////////
    // Static construction mechanisms
    ///////////////////////////////////////////////////////////////////////////

    public static TypeConstructionForm init(final Stage s,
                                            final Map<Field, Type> map) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final TypeConstructionForm form = loader.getController();

            form.root = root;
            form.displayStage = s;
            s.setScene(new Scene(form.root));
            map.forEach((form::getNodeRepresentationFor));

            return form;

            // Re-throw
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Setup + Event handles
    ///////////////////////////////////////////////////////////////////////////

    private void getNodeRepresentationFor(final Field f,
                                          final Type t) {
        final EnumerableValue[] v = t.getEnumerableValues();

        final Node n;
        if (!(v == null) && !(v.length == 0)) {
            final ChoiceBox<EnumerableValue> node = new ChoiceBox<>();
            node.getItems().addAll(v);
            node.getSelectionModel().select(v[0]);
            n = node;

        } else {
            final TextField field = new TextField();
            field.setTextFormatter(
                    new TextFormatter<>(t.getTextFieldHandler())
            );

            // Set a default value
            if (f.isAnnotationPresent(WritableField.class)) {
                final WritableField wf = f.getAnnotation(WritableField.class);
                field.setText(wf.defaultValue());
            }

            n = field;
        }

        final Tooltip tip = new Tooltip();
        tip.setShowDelay(Duration.ZERO);

        // Set display text
        if (f.isAnnotationPresent(WritableField.class)) {
            final WritableField wf = f.getAnnotation(WritableField.class);
            tip.setText(wf.name());
        } else {
            tip.setText(f.getName());
        }
        Tooltip.install(n, tip);

        SceneUtil.applyStyle(n);
        this.nodeFieldTypeMap.put(n, new Pair<>(f, t));
        typeChildFormVBox.getChildren().add(n);
    }

    @FXML
    private void onFinishClicked() {
        this.isNaturalExit = true;
        this.displayStage.close();
    }

    public void initDefaults(final Object instance)
            throws IllegalAccessException {

        for (final Map.Entry<Node, Pair<Field, Type>> entry
                : this.nodeFieldTypeMap.entrySet()) {
            final Node n = entry.getKey();
            final Pair<Field, Type> pair = entry.getValue();
            pair.getKey().setAccessible(true);

            final Object val = pair.getKey().get(instance);
            final Object enumerable = pair.getValue().enumerableOf(val);

            if (enumerable != null) {
                setValue(n, enumerable);
            } else {
                setValue(n, val);
            }
        }
    }

    private void setValue(final Node n,
                          final Object o) {
        if (n instanceof TextField t) {
            t.setText(String.valueOf(o));

        } else if (n instanceof ChoiceBox<?> box) {
            final int index = box.getItems().indexOf(o);
            if (index != -1) {
                box.getSelectionModel().select(index);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Standard data collection mechanisms
    ///////////////////////////////////////////////////////////////////////////

    public Parent getRoot() {
        return root;
    }

    public Stage getDisplayStage() {
        return displayStage;
    }

    public boolean isNaturalExit() {
        return isNaturalExit;
    }

    public void reset() {
        this.isNaturalExit = false;
        this.nodeFieldTypeMap.keySet().forEach(i -> {
            // Clear text
            if (i instanceof TextField t) {
                t.clear();

                // Reset selection
            } else if (i instanceof ChoiceBox<?> c) {
                c.getSelectionModel().select(0);
            }
        });
    }

    public List<Pair<Field, Object>> parseTypes()
            throws IncompleteFormException,
            TypeInstantiationException {

        final List<Pair<Field, Object>> fieldValueList = new ArrayList<>();
        for (final Map.Entry<Node, Pair<Field, Type>> entry
                : this.nodeFieldTypeMap.entrySet()) {

            final Node n = entry.getKey();
            final Pair<Field, Type> pair = entry.getValue();

            // Text field parse
            if (n instanceof final TextField f) {

                // Find any errors, if any.
                final String data = f.getText();
                if (data.equals("") || !pair.getValue().isComplete(data)) {
                    throw new IncompleteFormException(
                            "Text Field incomplete!"
                    );

                    // If none could be found attempt to parse the value.
                } else {
                    fieldValueList.add(new Pair<>(
                            pair.getKey(),
                            pair.getValue().construct(data)
                    ));
                }

                // Enumerated value type
            } else if (n instanceof final ChoiceBox<?> box) {

                // Construct the enumerable value
                final Object selected
                        = box.getSelectionModel().getSelectedItem();
                if (selected instanceof final EnumerableValue v) {
                    fieldValueList.add(new Pair<>(
                            pair.getKey(),
                            v.construct(pair.getValue())
                    ));

                    // Value is malformed
                } else {
                    throw new IncompleteFormException(
                            "Enumerable data field incomplete!!"
                    );
                }
            }
        }

        return fieldValueList;
    }
}
