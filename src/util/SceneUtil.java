package util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.function.UnaryOperator;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2. Class
 * wraps some scene utility functions that are generally bulky in code.
 *
 * @author -Ry
 * @version 0.4
 * Copyright: N/A
 */
public final class SceneUtil {

    /**
     * Duration of all fade effects in millis.
     */
    private static final short TRANSITION_EFFECT_DURATION = 350;

    /**
     * Unary operator wrapping Positive integer text formats. Note that the
     * base case of the Empty string must still be considered.
     */
    public static final UnaryOperator<TextFormatter.Change>
            POSITIVE_INTEGER_FORMAT = (e) -> {
        if (e.getControlNewText().matches("[0-9]*")) {
            return e;
        } else {
            return null;
        }
    };

    /**
     * Unary operator wrapping file name safe text formats. Note that the
     * base case of the Empty string must still be considered.
     */
    public static final UnaryOperator<TextFormatter.Change>
            FILE_NAME_FORMAT = (e) -> {
        if (e.getControlNewText().matches("[a-zA-Z0-9_ ]*")) {
            return e;
        } else {
            return null;
        }
    };

    /**
     * Hide constructor.
     */
    private SceneUtil() {
        // Hide this
    }

    /**
     * @param e Event to check.
     * @return {@code true} if the event is a right click action.
     */
    public static boolean wasRightClick(final MouseEvent e) {
        return e.getButton().equals(MouseButton.SECONDARY);
    }

    /**
     * @param e Event to check.
     * @return {@code true} if the event is a left click action.
     */
    public static boolean wasLeftClick(final MouseEvent e) {
        return e.getButton().equals(MouseButton.PRIMARY);
    }

    /**
     * @param e Event to check.
     * @return {@code true} if the event is a double click action.
     */
    public static boolean wasDoubleClick(final MouseEvent e) {
        return e.getClickCount() >= 2;
    }

    /**
     * Creates a fade effect, starts it on the given node, and then returns
     * the transition.
     *
     * @param n The node to start the effect for.
     * @return Started fade transition.
     */
    public static FadeTransition fadeInNode(final Node n) {
        final FadeTransition transition = new FadeTransition();
        transition.setNode(n);
        transition.setFromValue(0);
        transition.setToValue(1.0);
        transition.setDuration(Duration.millis(TRANSITION_EFFECT_DURATION));
        transition.setCycleCount(0);
        transition.playFromStart();

        return transition;
    }

    /**
     * Creates a fade effect, starts it on the given node, and then returns
     * the transition.
     *
     * @param n The node to start the effect for.
     * @return Started fade transition.
     */
    public static FadeTransition fadeNodeOut(final Node n) {
        final FadeTransition transition = new FadeTransition();
        transition.setNode(n);
        transition.setFromValue(1.0);
        transition.setToValue(0);
        transition.setDuration(Duration.millis(TRANSITION_EFFECT_DURATION));
        transition.setCycleCount(0);
        transition.playFromStart();

        return transition;
    }

    /**
     * Creates a scale transition for the target node, scaling from 0 to 1.
     * Using the default Scene parameters.
     *
     * @param n The node to scale in.
     * @return The started scale transition.
     */
    public static ScaleTransition scaleNodeIn(final Node n) {
        final ScaleTransition t = new ScaleTransition();
        t.setNode(n);
        t.setDuration(Duration.millis(TRANSITION_EFFECT_DURATION));

        t.setFromX(0.0);
        t.setFromY(0.0);
        t.setFromZ(0.0);

        t.setToX(1.0);
        t.setToY(1.0);
        t.setToZ(1.0);

        t.playFromStart();
        return t;
    }

    /**
     * Applies the default form style classes to the target node.
     *
     * @param n The node to apply the style classes to.
     */
    public static void applyStyle(final Node n) {
        n.getStyleClass().addAll(
                "AltFontB",
                "DefaultText",
                "ContentModule"
        );
    }
}
