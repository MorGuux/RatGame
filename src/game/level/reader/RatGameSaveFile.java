package game.level.reader;

import game.entity.Entity;
import game.generator.RatItemGenerator;
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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class RatGameSaveFile extends RatGameFile {

    // todo learn how to comment

    /**
     *
     */
    private static final String ERR_DEFAULT_FILE_MISSING = "Could not find the"
            + "Default file for: \"%s\" the relative path: \"%s\" produced the"
            + "path: \"%s\" but that file does not exist or is not a file.";

    /**
     *
     */
    private enum SaveFileAspect implements RegexModule {

        /**
         *
         */
        PLAYER_DATA(Pattern.compile("(?is)(PLAYER_DATA|PLAYERDATA).*?\\{.*?}")),

        /**
         *
         */
        DEFAULT_FILE(Pattern.compile("(?i)@DEFAULT_FILE:(?-i)(.*?);"));

        /**
         *
         */
        private final Pattern regex;

        /**
         * @param regex
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
     * @param f
     * @return
     * @throws IOException
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

        final Pattern contentMatcher = Pattern.compile("(?is)NAME:(.*?);"
                + ".*?SCORE:\\s*([0-9]+);");
        final int nameGroup = 1;
        final int scoreGroup = 2;
        final int timePlayedGroup = 3;
        final Matcher m = contentMatcher.matcher(moduleContent);

        if (m.find()) {
            try {
                return new Player(
                        m.group(nameGroup),
                        Integer.parseInt(m.group(scoreGroup)),
                        null,
                        null
                );

                // Content improper
            } catch (Exception e) {
                throw new InvalidArgsContent(moduleContent);
            }

            // Content missing
        } else {
            throw new InvalidModuleContentException(moduleContent);
        }
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

    public static void main(String[] args) throws UnknownSpriteEnumeration, RatGameFileException, IOException {
        final File saveFile = new File(
                "src/game/level/levels/saves/levelOne.rgs"
        );

        final RatGameSaveFile file = new RatGameSaveFile(saveFile);

        file.getEntityPositionMap().keySet().forEach(i -> System.out.println(i.buildToString(null)));
        System.out.println();

        RatItemGenerator gen = file.getSaveFileGenerator();
        System.out.println(gen.buildAllToString());
    }
}
