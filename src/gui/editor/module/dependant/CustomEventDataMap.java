package gui.editor.module.dependant;

import javafx.scene.input.DataFormat;

import java.util.HashMap;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class CustomEventDataMap extends HashMap<DataFormat, Object> {

    /**
     * Data format of the event type.
     */
    public static DataFormat CONTENT_ID = new DataFormat("content/id");

    /**
     * Data format of the content held within.
     */
    public static DataFormat CONTENT = new DataFormat("content");

    /**
     * Constructs the Custom event data map from the provided args.
     *
     * @param contentID The ID of the event.
     * @param content   The event content.
     */
    public CustomEventDataMap(final String contentID,
                              final String content) {
        super();
        this.put(CONTENT_ID, contentID);
        this.put(CONTENT, content);
    }
}
