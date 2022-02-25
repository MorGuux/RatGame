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

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = TypeConstructionForm.class.getResource(
            "TypeConstructionForm.fxml");

    /**
     * VBox which consists of all the child Type data collection nodes.
     */
    @FXML
    private VBox typeChildFormVBox;

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Map that maps a data collection Node to its pair of Field, and Type
     * parser. Allowing us to parse the data held within the node, to the
     * target type, then write it to the specified field.
     */
    private final Map<Node, Pair<Field, Type>> nodeFieldTypeMap
            = new HashMap<>();

    /**
     * The root node of this scene.
     */
    private Parent root;

    /**
     * The stage that this scene/form will be displayed in.
     */
    private Stage displayStage;

    /**
     * Determines if the form was exited through the normal means. I.e., the
     * user clicked the button to continue they didn't just click the X.
     */
    private boolean isNaturalExit = false;

    ///////////////////////////////////////////////////////////////////////////
    // Static construction mechanisms
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Initialises the form using the stage to display in and the map
     * consisting of arbitrary types and a field of which it should be
     * written to.
     * <p>
     * Note that the types of the Field, or specifically {@link Type} are
     * only supported for what is contained within
     * {@link Type#SUPPORTED_TYPES} any type of Field which isn't in this
     * scope is not supported.
     *
     * @param s   The stage to display in.
     * @param map The map consisting of the Field and their Respective Type
     *            which handles the parsing of arbitrary text data.
     * @return Newly constructed form.
     */
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

    /**
     * Takes a Field and its appropriate type then creates the correct Node
     * that will allow the data to be collected for the target type.
     * <p>
     * Specifically if {@link Type#getEnumerableValues()} yields Not-null and
     * at least one value then a Choicebox is chosen, else a basic Textfield
     * is chosen instead.
     *
     * @param f The field to create a Node for.
     * @param t The type that contains information on which Node to choose
     *          and what constraints to apply.
     */
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

    /**
     * Natural form exit handler, this sets the natural exit state and then
     * terminates the form/stage.
     */
    @FXML
    private void onFinishClicked() {
        this.isNaturalExit = true;
        this.displayStage.close();
    }

    /**
     * Loads into this form the data held within the Object instance using
     * the provided Map of Field, Types. Effectively allowing us to populate
     * the fields/Nodes using the data held within the provided instance.
     *
     * @param instance The instance to collect data from and fill in the
     *                 specified fields.
     * @throws IllegalAccessException   If access to the specified method is
     *                                  denied even after suppressing Java
     *                                  Language checks.
     * @throws IllegalArgumentException If any of the fields provided at
     *                                  construction don't exist in the
     *                                  provided instance.
     */
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

    /**
     * Sets the value of the data held at the provided Node to the provided
     * object.
     *
     * @param n The node to set the value for.
     * @param o The value to set.
     */
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

    /**
     * @return Root node of the scene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return Stage that this scene is being displayed in.
     */
    public Stage getDisplayStage() {
        return displayStage;
    }

    /**
     * @return {@code true} if the form exited naturally in that the user has
     * clicked the close form button. Else, {@code false} if the form has not
     * been terminated or if the form was closed using the red X.
     */
    public boolean isNaturalExit() {
        return isNaturalExit;
    }

    /**
     * Resets this form allowing it to be re-used.
     */
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

    /**
     * Parses all of the Node representations of the {@link Field} and
     * {@link Type} pairs which was passed in at construction.
     *
     * @return List of pairs, mapping a Field to its Parsed Object type.
     * @throws IncompleteFormException    If the form is not complete, in that
     *                                    one or more fields failed the check
     *                                    {@link Type#isComplete(String...)}.
     * @throws TypeInstantiationException If type object construction fails,
     *                                    this occurs when a data field had
     *                                    the correct data but broke the scope
     *                                    of its container object such as an
     *                                    Integer exceeding 2^31.
     */
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
