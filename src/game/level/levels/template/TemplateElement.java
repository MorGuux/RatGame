package game.level.levels.template;

import java.util.regex.Pattern;

/**
 * Java enum created on 10/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public enum TemplateElement {

    /**
     * Represents the Friendly name element in a default file.
     */
    FRIENDLY_NAME(false),

    /**
     * Represents the maximum row count element in a default file.
     */
    MAP_ROW_COUNT(false),

    /**
     * Represents the maximum col count element in a default file.
     */
    MAP_COL_COUNT(false),

    /**
     * Represents the maximum rats element in a default file.
     */
    MAX_RATS(false),

    /**
     * Represents the time limit element in a default file.
     */
    TIME_LIMIT(false),

    /**
     * Represents the leaderboard of players element in a default file.
     */
    LEADERBOARD(true),

    /**
     * Represents the item generators/inventory element in a default file.
     */
    ITEM_GENERATOR(true),

    /**
     * Represents the existing entities' element in a default file.
     */
    ENTITY_INSTANCES(true),

    /**
     * Represents the tiles held in the map element in a default file.
     */
    MAP_LAYOUT(true),

    /**
     * Level identifier enumeration.
     */
    LEVEL_ID_NAME(false);

    /**
     * Regex which matches the target ordinal in a default file.
     */
    private final Pattern regex;

    /**
     * Pad character used to keep things like indenting and new lines correct.
     */
    private final String padChar;

    /**
     * Enumeration constructor.
     *
     * @param isFullBodyMatch Are we editing a property or are we editing a
     *                        full body element?
     */
    TemplateElement(final boolean isFullBodyMatch) {

        // Below is for #ABC: CONTENT;
        if (!isFullBodyMatch) {
            regex = Pattern.compile(String.format(
                    "(?i)(?<=#%s:).*?(?=;)",
                    Pattern.quote(name())
            ));
            padChar = "";

            // Below is for ABC { CONTENT }
        } else {
            this.regex = Pattern.compile(String.format(
                    "(?i)(?<=%s\\s\\{)(\\s*)(?s).*?(?=})",
                    Pattern.quote(name())
            ));
            padChar = System.lineSeparator();
        }
    }

    /**
     * @return Regex which matches elements of the target ordinal.
     */
    public Pattern getRegex() {
        return regex;
    }

    /**
     * @return Pad character which is used to keep new lines and spacing
     * consistent.
     */
    public String getPadChar() {
        return padChar;
    }
}
