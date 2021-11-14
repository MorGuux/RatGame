package gui.assets.css;

import java.net.URL;

/**
 * Enumerates the various Styles available for our Application.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public enum SceneStyle {
    /**
     * Alternate dark theme style. Not really a dark theme but a darker light.
     */
    ALT_DARK_THEME(SceneStyle.class.getResource("alt_dark_theme.css")),

    /**
     * Base dark theme which is the default.
     */
    DARK_THEME(SceneStyle.class.getResource("dark_theme.css")),

    /**
     * Light theme which nobody should use.
     */
    LIGHT_THEME(SceneStyle.class.getResource("light_theme.css"));

    /**
     * Resource location of the stylesheet.
     */
    private final URL resource;

    /**
     * @param cssResource CSS File resource.
     */
    SceneStyle(final URL cssResource) {
        resource = cssResource;
    }

    /**
     * Warning: Always check for {@code Nullity} with these files.
     *
     * @return CSS File as a resource, or {@code null} if the resource failed
     * to load correctly.
     */
    public URL getResource() {
        return this.resource;
    }
}
