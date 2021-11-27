package game.level.reader;

import game.entity.Entity;
import game.generator.RatItemGenerator;
import game.level.levels.RatGameLevel;
import game.level.reader.exception.InvalidArgsContent;
import game.level.reader.exception.InvalidModuleContentException;
import game.level.reader.exception.MissingModuleException;
import game.level.reader.exception.RatGameFileException;
import game.player.Player;
import game.tile.exception.UnknownSpriteEnumeration;
import gui.game.dependant.tilemap.Coordinates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Wraps a Rat Game Save file, where the save file only differs in the
 * existing entities, existing item generator, and current player.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class RatGameSaveFile extends RatGameFile {

    // todo learn how to comment

    /**
     * States that the default file is missing from the provided file meaning
     * not all the data could be loaded.
     */
    private static final String ERR_DEFAULT_FILE_MISSING = "Could not find the"
            + "Default file for: \"%s\" the relative path: \"%s\" produced the"
            + "path: \"%s\" but that file does not exist or is not a file.";

    /**
     * Represents a module in the save file.
     *
     * @author -Ry
     * @version 0.1
     * Copyright: N/A
     */
    private enum SaveFileAspect implements RegexModule {

        /**
         * Player data module containing all the relevant information about
         * the player.
         */
        PLAYER_DATA(Pattern.compile("(?is)(PLAYER_DATA|PLAYERDATA).*?\\{.*?}")),

        /**
         * Default file section that contains the default level file. Which
         * that file contains the game properties and the tile map.
         */
        DEFAULT_FILE(Pattern.compile("(?i)@DEFAULT_FILE:(?-i)(.*?);"));

        /**
         * Regex which matches the target module.
         */
        private final Pattern regex;

        /**
         * @param regex Regex to match the target module
         */
        SaveFileAspect(final Pattern regex) {
            this.regex = regex;
        }

        /**
         * @return Regex that will match this module.
         */
        @Override
        public Pattern getRegex() {
            return regex;
        }
    }

    /**
     * Player tag represents all aspects of the Player data module.
     *
     * @author -Ry
     * @version 0.1
     * Copyright: N/A
     */
    private enum PlayerTag implements RegexModule {
        /**
         * The players name.
         */
        NAME("(.*?)"),

        /**
         * The players score.
         */
        SCORE("([0-9]+)"),

        /**
         * The time elapsed on the level by the player.
         */
        TIME_PLAYED("([0-9]+)"),

        /**
         * The levels that the player has unlocked.
         */
        LEVELS_UNLOCKED("\\[(.*?)]");

        /**
         * Regex to match the tag.
         */
        private final Pattern regex;

        /**
         * Constructs the player tag from the expected data regex.
         *
         * @param data Partial Regex which matches the expected data for the
         *             tag.
         */
        PlayerTag(final String data) {
            final String base = "(?i)(%s):\\s*%s;";

            regex = Pattern.compile(String.format(
                    base,
                    name() + "|" + name().replaceAll("_", ""),
                    data
            ));
        }

        /**
         * @return Regex that will match this module.
         */
        @Override
        public Pattern getRegex() {
            return regex;
        }

        /**
         * @return Group value that matches the content held within this tag.
         */
        public int getContentGroup() {
            return 2;
        }
    }

    /**
     * Gets the default file from the provided save file.
     *
     * @param f Save file to get the default file from.
     * @return Default file
     * @throws IOException            If any occur whilst reading the provided
     *                                file.
     * @throws MissingModuleException If the Default file module is missing.
     */
    private static File getDefaultFile(final File f)
            throws IOException, MissingModuleException {
        // Setup
        final String content = Files.lines(f.toPath())
                .collect(Collectors.joining());
        final Pattern regex = SaveFileAspect.DEFAULT_FILE.getRegex();
        final Matcher m = regex.matcher(content);

        // Obtain
        if (m.find()) {
            final String path = m.group(1).trim();

            // Parse for file relative from the save file
            final File found = new File(
                    f.getParentFile().getAbsolutePath()
                            + "/"
                            + path
            );


            // Return found
            if (found.isFile()) {
                return found;

                // Not found
            } else {
                throw new FileNotFoundException(String.format(
                        ERR_DEFAULT_FILE_MISSING,
                        f.getAbsolutePath(),
                        path,
                        found.getAbsolutePath()
                ));
            }

            // Could not find module
        } else {
            throw new MissingModuleException(String.format(
                    ERR_MISSING_MODULE,
                    SaveFileAspect.DEFAULT_FILE.name(),
                    f.getAbsolutePath()
            ));
        }
    }

    /**
     *
     */
    private final String saveFile;

    /**
     *
     */
    private final String fileContent;

    /**
     *
     */
    private final RatItemGenerator saveFileGenerator;

    /**
     *
     */
    private final Player player;

    /**
     *
     */
    private final HashMap<Entity, List<Coordinates<Integer>>> entityPosMap;

    /**
     * Loads up a rat game file so that its content can be parsed.
     *
     * @param saveFile The rat game file to load.
     * @throws NullPointerException If the provided file is
     *                              {@code null}.
     * @throws IOException          If one occurs whilst attempting to
     *                              read the provided Game file.
     * @throws RatGameFileException If the any of the file content is
     *                              improperly setup.
     */
    public RatGameSaveFile(final File saveFile)
            throws IOException, RatGameFileException, UnknownSpriteEnumeration {
        super(RatGameSaveFile.getDefaultFile(saveFile));
        this.saveFile = saveFile.getAbsolutePath();
        this.fileContent = Files.lines(
                saveFile.toPath()).collect(Collectors.joining()
        );

        // A save file requires these exist
        final RegexModule[] required = {
                SaveFileAspect.DEFAULT_FILE,
                SaveFileAspect.PLAYER_DATA,
                Module.ITEM_GENERATOR,
                Module.ENTITY_INSTANCES
        };
        super.ensureModulePresence(required, this.fileContent);

        this.saveFileGenerator = super.loadItemGenerator(this.fileContent);
        this.entityPosMap = super.loadEntities(this.fileContent);
        this.player = loadPlayer(this.fileContent);
    }

    /**
     * Loads the current player from the save file.
     *
     * @param content The content to load the player from.
     * @return Constructed player using the args defined in the content.
     */
    private Player loadPlayer(final String content)
            throws InvalidArgsContent, InvalidModuleContentException {
        //todo figure out player
        final String moduleContent =
                super.getModuleContent(SaveFileAspect.PLAYER_DATA,
                        content, 0);

        try {
            final int contentGroup = 2;
            final String name = getModuleContent(
                    PlayerTag.NAME, moduleContent, contentGroup
            );
            final int score = Integer.parseInt(
                    getModuleContent(
                            PlayerTag.SCORE, moduleContent, contentGroup
                    ));
            final int timePlayed = Integer.parseInt(
                    getModuleContent(
                            PlayerTag.TIME_PLAYED, moduleContent, contentGroup
                    ));
            final ArrayList<RatGameLevel> unlockedLevels = loadUnlockedLevels(
                    getModuleContent(
                            PlayerTag.LEVELS_UNLOCKED,
                            moduleContent,
                            contentGroup
                    ));

            return new Player(name,
                    score,
                    timePlayed,
                    unlockedLevels,
                    super.getLevel()
            );

            // Rethrow parse exceptions
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    e.getMessage()
            );
        }
    }

    /**
     * @param rawLevels The raw unparsed levels.
     * @return Levels unlocked by this player as an attachable resource.
     * @throws InvalidArgsContent If there is an unknown Level constant.
     */
    private ArrayList<RatGameLevel> loadUnlockedLevels(final String rawLevels)
            throws InvalidArgsContent {
        final ArrayList<RatGameLevel> levels = new ArrayList<>();
        final String[] args = rawLevels
                .replaceAll("\\s", "")
                .split(",");

        try {
            for (String arg : args) {
                levels.add(RatGameLevel.valueOf(arg));
            }
        } catch (Exception e) {
            throw new InvalidArgsContent(Arrays.deepToString(args));
        }

        return levels;
    }

    /**
     * @return The item generator for the save file.
     */
    public RatItemGenerator getSaveFileGenerator() {
        return this.saveFileGenerator;
    }

    /**
     * @return The entity position mapping for the save file.
     */
    public HashMap<Entity, List<Coordinates<Integer>>> getEntityPositionMap() {
        return this.entityPosMap;
    }

    /**
     * @return The player who was playing the game for this save.
     */
    public Player getPlayer() {
        return this.player;
    }
}
