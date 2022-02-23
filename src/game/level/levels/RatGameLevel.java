package game.level.levels;

import game.level.reader.RatGameFile;

import java.io.File;
import java.net.URL;
import java.util.Objects;

/**
 * Known Rat Game Levels that can be played.
 *
 * @author -Ry
 * @version 0.4
 * Copyright: N/A
 */
public enum RatGameLevel {

    /**
     * Level one of the Rat game.
     */
    LEVEL_ONE(RatGameLevel.class.getResource("LevelOne.rgf")),
    /**
     * Level two of the Rat game.
     */
    LEVEL_TWO(RatGameLevel.class.getResource("LevelTwo.rgf")),
    /**
     * Level three of the Rat game.
     */
    LEVEL_THREE(RatGameLevel.class.getResource("LevelThree.rgf")),
    /**
     * Debug level used for testing of features of the Rat game.
     */
    DEBUG_LEVEL(RatGameLevel.class.getResource("DebugLevel.rgf")),
    /**
     * Specific testing level for the Rat game.
     */
    TEST_CLEAR_LEVEL(RatGameLevel.class.getResource("TestClearLevel.rgf")),
    /**
     * Specific testing level for the Rat game.
     */
    TEST_PATH_LEVEL(RatGameLevel.class.getResource("TestPathLevel.rgf")),
    /**
     * Specific testing level for the Rat game.
     */
    TEST_TUNNEL_LEVEL(RatGameLevel.class.getResource("TestTunnelLevel.rgf"));

    /**
     * Custom save file, saves directory.
     */
    public static final String CUSTOM_LEVEL_SAVES_DIR
            = "src/game/level/levels/template/custom/saves";

    /**
     * Error message for when the level is unknown.
     */
    private static final String ERR_UNKNOWN_NAME = "The name: \"%s\" is an "
            + "Unknown Level...";

    /**
     * The directory that all save files should be placed into.
     */
    public static final String SAVES_DIR = "src/game/level/levels/saves";

    /**
     * Resource of the default rat game level file.
     */
    private final URL defaultLevel;

    /**
     * Checks to see if there is an enum constant with the provided name;
     * matching case-insensitive.
     *
     * @param name The name to find a match for.
     * @return The match found.
     * @throws IllegalArgumentException If no match was found.
     */
    public static RatGameLevel getLevelFromName(final String name) {
        for (RatGameLevel level : RatGameLevel.values()) {

            // Remove spaces and Underscores from the enum name
            final String lName
                    = level.name().replaceAll("[\s_]", "");

            // Name without spaces
            final String safeName
                    = name.replaceAll("[\s_]", "");

            if (lName.matches("(?i)" + safeName)) {
                return level;
            }
        }
        // No matching name
        throw new IllegalArgumentException(String.format(
                ERR_UNKNOWN_NAME,
                name
        ));
    }

    /**
     * Constructs the ordinal from the default file resource.
     *
     * @param file Default rat game level file.
     */
    RatGameLevel(final URL file) {
        Objects.requireNonNull(file);
        this.defaultLevel = file;
    }

    /**
     * @return Resource attached to the Rat game level file.
     */
    public URL getDefaultLevel() {
        return defaultLevel;
    }

    /**
     * Parses the associated target level into a {@link RatGameFile} so that
     * all modules can be accessed.
     *
     * @return Rat game file of the target level.
     */
    public RatGameFile getRatGameFile() {
        try {
            return new RatGameFile(new File(
                    this.getDefaultLevel().toURI().getPath()
            ));

            // Shouldn't occur
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
