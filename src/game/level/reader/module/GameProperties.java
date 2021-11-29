package game.level.reader.module;

import game.level.reader.RatGameFile;
import game.level.reader.exception.InvalidModuleContentException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wraps all the game properties held within a {@link RatGameFile}.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class GameProperties {

    /**
     * Error message to help deduce errors within the Game properties.
     */
    private static final String ERR_MISSING_PROPERTY = "Property: \"%s\" "
            + "could not be found in Module content:%n\"%s\"%n";

    /**
     * Represents an underlying property of the Rat Game, Game Properties.
     */
    private enum Property implements RatGameFile.RegexModule {
        /**
         * The number of rows a map has (Height).
         */
        MAP_ROW_COUNT(),

        /**
         * The number of columns a map has (Width).
         */
        MAP_COL_COUNT(),

        /**
         * The maximum number of rats to allow into the game.
         */
        MAX_RATS(),

        /**
         * Time limit property inorder to be awarded bonus points.
         */
        TIME_LIMIT();

        /**
         * Regex for matching the literal property.
         */
        private final Pattern regex;

        Property() {
            final String base = "(?i)#%s:.*?([0-9]+);";
            this.regex = Pattern.compile(
                    String.format(base, name())
            );
        }

        // Can modify this if more properties are added but not needed right now
        public int getCaptureGroup() {
            return 1;
        }

        @Override
        public Pattern getRegex() {
            return regex;
        }
    }

    /**
     * The raw unparsed module content.
     */
    private final String moduleContent;

    /**
     * The number of rows a map has (Height).
     */
    private final int rows;

    /**
     * The number of columns a map has (Width).
     */
    private final int columns;

    /**
     * The maximum number of rats to allow into the game.
     */
    private final int maxRats;

    /**
     * Time limit property inorder to be awarded bonus points.
     */
    private final int timeLimit;

    /**
     * The friendly name of the level.
     */
    private final String levelName;

    /**
     * Constructs the game properties from a Game properties file module.
     *
     * @param moduleContent Content held within the game property module that
     *                      is to be parsed.
     */
    public GameProperties(final String moduleContent, final String levelName)
            throws InvalidModuleContentException {
        Objects.requireNonNull(moduleContent);

        this.moduleContent = moduleContent;

        this.levelName = levelName;

        // Parse properties
        this.rows = Integer.parseInt(
                getProperty(Property.MAP_ROW_COUNT)
        );

        this.columns = Integer.parseInt(
                getProperty(Property.MAP_COL_COUNT)
        );

        this.maxRats = Integer.parseInt(
                getProperty(Property.MAX_RATS)
        );

        this.timeLimit = Integer.parseInt(
                getProperty(Property.TIME_LIMIT)
        );
    }

    /**
     * Parses the single property for its literal content trimming any
     * leading or trailing whitespaces.
     *
     * @param property The property to get.
     * @return Trimmed string.
     * @throws InvalidModuleContentException If the property does not exist
     *                                       in the module content.
     */
    private String getProperty(final Property property)
            throws InvalidModuleContentException {
        final Matcher m = property.getRegex().matcher(moduleContent);

        if (m.find()) {
            return m.group(property.getCaptureGroup()).trim();
        } else {
            throw new InvalidModuleContentException(String.format(
                    ERR_MISSING_PROPERTY,
                    property.name(),
                    moduleContent
            ));
        }
    }

    /**
     * @return Raw module content used to construct this.
     */
    public String getModuleContent() {
        return moduleContent;
    }

    /**
     * @return The number of rows the map should have.
     */
    public int getRows() {
        return rows;
    }

    /**
     * @return The number of columns the map should have.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @return The maximum number of rats the game should have.
     */
    public int getMaxRats() {
        return maxRats;
    }

    /**
     * @return Time limit in milliseconds required to clear the level in
     * order to be awarded bonus points.
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * @return The friendly name of the level.
     */
    public String getLevelName() {
        return levelName;
    }
}
