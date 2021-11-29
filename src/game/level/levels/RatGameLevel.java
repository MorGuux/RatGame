package game.level.levels;

import game.level.reader.RatGameFile;

import java.io.File;
import java.net.URL;
import java.util.Objects;

/**
 * Known Rat Game Levels that can be played.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public enum RatGameLevel {

    /**
     * Level one of the Rat game.
     */
    LEVEL_ONE(RatGameLevel.class.getResource("LevelOne.rgf"));

    /**
     * Resource of the default rat game level file.
     */
    private final URL defaultLevel;

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
            return new RatGameFile(new File(this.getDefaultLevel().toURI().getPath()));

            // Shouldn't occur
        } catch (Exception e) {
            throw new IllegalStateException(
                    e.getMessage()
            );
        }
    }
}
