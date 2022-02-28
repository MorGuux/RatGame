package gui.editor.module.dependant;

import javafx.scene.input.DataFormat;

import java.util.HashMap;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2.
 * Is responsible for generating an event hashmap given the parameters given.
 * @author -Ry
 */
public class CustomEventDataMap extends HashMap<DataFormat, Object> {

    ///////////////////////////////////////////////////////////////////////////
    // The content held within these formats must be Serializable all that
    // means is that the Class implements Serializable and all members of it
    // also implement serializable which in our case isn't true since we
    // never had a need for Object serialization.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Data format of the event type.
     */
    public static final DataFormat CONTENT_ID
            = new DataFormat("content/id");

    /**
     * Data format of the content held within.
     */
    public static final DataFormat CONTENT
            = new DataFormat("content");

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
