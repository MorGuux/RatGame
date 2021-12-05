package game.level.savecontext;

import game.entity.Item;
import game.generator.ItemGenerator;
import game.generator.RatItemInventory;
import game.level.levels.RatGameLevel;
import game.level.reader.RatGameFile;
import game.level.reader.RatGameSaveFile;
import game.level.reader.exception.RatGameFileException;
import game.player.Player;
import game.tile.exception.UnknownSpriteEnumeration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Class provides the ability to save the current state of a set of static
 * modules which can then be loaded back into their finalised/objective state.
 * The modules of relevance that are saved here are the following:
 * <ol>
 *     <li>Default File reference</li>
 *     <li>Player Data</li>
 *     <li>Generator State</li>
 *     <li>Entity State</li>
 * </ol>
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public class RatGameSaveContext {

    /**
     * Save context format version.
     */
    private static final int RGS_CONTEXT_VER = 1;

    /**
     * Header string for the save format version.
     */
    private static final String HEADER_INFO =
            "// - - - - VER: %s - - - -%n";

    /**
     * The extension that all save files have; acronym stands for Rat Game
     * Save File.
     */
    private static final String SAVE_FILE_EXTENSION = ".rgs";

    /**
     * The header of all save files should be this. This does nothing at all
     * however but is a means of an identifier.
     */
    private static final String SAVE_HEADER = "FILE_TYPE: Save File";

    /**
     * Default file reference as all save files are based upon save default
     * file.
     */
    private static final String DEFAULT_FILE_REF = "@DEFAULT_FILE: %s;%n";

    /**
     * Template module for saving a player to a file. The Levels unlocked
     * section is kinda redundant as it is not used and this could be
     * replaced with a call to the
     * {@link game.level.levels.players.PlayerDataBase} searching for the
     * user by their name.
     */
    private static final String TEMPLATE_PLAYER_MODULE
            = "PLAYER_DATA {%n    NAME: %s;%n    SCORE: %s;%n    "
            + "TIME_PLAYED:%s;%n    LEVELS_UNLOCKED:%s;%n}%n";

    /**
     * Template module container for all the Item generators of this specific
     * Rat game level.
     */
    private static final String TEMPLATE_ITEM_GENERATOR
            = "ITEM_GENERATOR {%n%s%n}%n";

    /**
     * Template module container for all the entity instances that are to be
     * placed into the Rat game.
     */
    private static final String TEMPLATE_ENTITY_INSTANCES
            = "ENTITY_INSTANCES {%n%s%n}%n";

    /**
     * Path to the place where we will save this specific players save
     * information.
     */
    private final String pathToSaveAt;

    /**
     * Path to the default file that the save file is based upon.
     */
    private final String pathToDefaultFile;

    /**
     * The player whose playing the game.
     */
    private final Player targetPlayer;

    /**
     * Initialise the save context for a new save file. Makes one where it
     * can.
     *
     * @param p The target player for the save file.
     */
    public RatGameSaveContext(final Player p)
            throws UnknownSpriteEnumeration,
            RatGameFileException,
            IOException {
        this.targetPlayer = p;
        this.pathToSaveAt = reserveSaveFile();
        this.pathToDefaultFile = p.getLevel().getLevelFile();
    }

    /**
     * Initiates the save context from a known save file.
     *
     * @param existingSaveFile The save file to save to.
     * @param player           The player to save.
     */
    public RatGameSaveContext(final RatGameSaveFile existingSaveFile,
                              final Player player) {
        this.pathToSaveAt = existingSaveFile.getSaveFile();
        this.targetPlayer = player;
        this.pathToDefaultFile = player.getLevel().getLevelFile();
    }

    /**
     * Reserves for the target level a save file index that it can use.
     *
     * @return Path to a newly constructed file ready to be saved into.
     */
    private String reserveSaveFile()
            throws UnknownSpriteEnumeration,
            RatGameFileException,
            IOException {
        final File dir = new File(RatGameLevel.SAVES_DIR);
        final File[] files = dir.listFiles();

        if (files != null) {
            final RatGameFile defaultFile
                    = targetPlayer.getLevel().getAsRatGameFile();
            final String levelName =
                    defaultFile.getDefaultProperties().getIdentifierName();

            final List<File> savesForThisLevel = new ArrayList<>();
            Arrays.stream(files)
                    // Filter to file's that are of this level
                    .filter(i -> i.isFile()
                            && i.getName().endsWith(SAVE_FILE_EXTENSION)
                            && i.getName().contains(levelName))
                    // Collect them
                    .forEach(savesForThisLevel::add);

            final String fileName = getSafeFileName(
                    savesForThisLevel,
                    levelName
            );

            final File saveFile = new File(
                    RatGameLevel.SAVES_DIR
                            + "/"
                            + fileName
            );

            // If the save file could not be created
            if (!saveFile.createNewFile() && !saveFile.isFile()) {
                throw new IOException();

            } else {
                return saveFile.getAbsolutePath();
            }

            // Saves directory failed to load
        } else {
            throw new IOException();
        }
    }

    /**
     * Filters through a list of save files and for all of them will attempt
     * find a place where it can reserve a name, safely.
     *
     * @param savesForThisLevel All known saves for the target level.
     * @param levelName         The name of the level we're creating a save for.
     * @return Save name that can be reserved in the users file system.
     */
    private String getSafeFileName(final List<File> savesForThisLevel,
                                   final String levelName) {

        final String baseFileName = "%s-%s.rgs";

        final int maxSaveFiles = 50;
        for (int i = 0; i < maxSaveFiles; ++i) {
            final String fileName = String.format(
                    baseFileName,
                    levelName,
                    i
            );

            if (isUniqueFileName(fileName, savesForThisLevel)) {
                return fileName;
            }
        }

        // Too many save files
        throw new IllegalStateException();
    }

    /**
     * Tests to see if the provided filename is a unique filename that only
     * does not occur in the provided list of files.
     *
     * @param fileName          The filename to check for uniqueness.
     * @param savesForThisLevel The files to test against.
     * @return {@code true} if the filename does not appear in the provided
     * list of files.
     */
    private boolean isUniqueFileName(final String fileName,
                                     final List<File> savesForThisLevel) {
        for (File i : savesForThisLevel) {
            if (i.getName().equals(fileName)) {
                return false;
            }
        }
        // Default exit couldn't prove it didn't exist.
        return true;
    }

    /**
     * Saves into the target save file the parsed game data.
     *
     * @param inventory        The rat game inventory containing the item
     *                         generators that are to be saved in the file.
     * @param rawBuiltEntities The raw built entities that can be saved as is
     *                         into the entities' module.
     */
    public void saveGameInfo(final RatItemInventory inventory,
                             final String rawBuiltEntities)
            throws IOException,
            UnknownSpriteEnumeration,
            RatGameFileException {

        final String fileData = buildHeaderInfo()
                + buildPlayerDataModule()
                + buildItemGeneratorModule(inventory.getGenerators())
                + buildEntityInstanceModule(rawBuiltEntities);

        Files.writeString(
                Path.of(pathToSaveAt),
                fileData,
                StandardCharsets.UTF_8
        );
    }

    /**
     * Creates the top header information for the save file. Setting up the
     * default file paths.
     *
     * @return Header info section containing the Default file reference and
     * file declaration.
     */
    private String buildHeaderInfo()
            throws UnknownSpriteEnumeration,
            RatGameFileException,
            IOException {
        final StringJoiner sj = new StringJoiner(System.lineSeparator());

        sj.add(String.format(HEADER_INFO, RGS_CONTEXT_VER));
        sj.add(SAVE_HEADER);

        final RatGameFile defaultFile =
                targetPlayer.getLevel().getAsRatGameFile();
        sj.add(String.format(DEFAULT_FILE_REF,
                "../"
                        + defaultFile.getDefaultProperties().getIdentifierName()
                        + ".rgf"
        ));

        return sj.toString();
    }

    /**
     * @return Player module populated with the target players information.
     */
    private String buildPlayerDataModule() {
        return String.format(
                TEMPLATE_PLAYER_MODULE,
                this.targetPlayer.getPlayerName(),
                this.targetPlayer.getCurrentScore(),
                this.targetPlayer.getPlayTime(),
                Arrays.deepToString(this.targetPlayer.getLevelsUnlocked())
        );
    }

    /**
     * Builds all the item generators to a String and wraps them in a Rat
     * Item Generator module.
     *
     * @param generators The generators to build into the module.
     * @return Parsed and formatted module string.
     */
    private String buildItemGeneratorModule(
            final List<ItemGenerator<? extends Item>> generators) {
        final StringJoiner sj
                = new StringJoiner(System.lineSeparator());

        for (ItemGenerator<?> generator : generators) {
            sj.add("    " + generator.buildToString());
        }


        return String.format(TEMPLATE_ITEM_GENERATOR, sj);
    }

    /**
     * Compiles all the raw entities into a file module that can be saved
     * into the target save file.
     *
     * @param rawBuiltEntities The entities to add to the file.
     * @return Entities wrapped in a file module.
     */
    private String buildEntityInstanceModule(final String rawBuiltEntities) {
        return String.format(TEMPLATE_ENTITY_INSTANCES, rawBuiltEntities);
    }
}
