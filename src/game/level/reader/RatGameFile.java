package game.level.reader;

import game.entity.Entity;
import game.entity.loader.EntityLoader;
import game.generator.ItemGenerator;
import game.generator.RatItemGenerator;
import game.generator.loader.ItemGeneratorLoader;
import game.level.Level;
import game.level.reader.exception.DuplicateModuleException;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.level.reader.exception.InvalidModuleContentException;
import game.level.reader.exception.MissingModuleException;
import game.level.reader.exception.RatGameFileException;
import game.level.reader.module.GameProperties;
import game.tile.Tile;
import game.tile.exception.UnknownSpriteEnumeration;
import game.tile.loader.TileLoader;
import gui.game.dependant.tilemap.Coordinates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a default rat game file and all aspects that should be inside the
 * default file.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class RatGameFile {

    /**
     * Error message for duplicate modules in a rat game file.
     */
    protected static final String ERR_DUPLICATE_MODULE = "Duplicate reference"
            + " to File property: \"%s\" in file: \"%s\"...";

    /**
     * Error message for missing modules in a rat game file.
     */
    private static final String ERR_MISSING_MODULE = "Module: \"%s\" does not"
            + " exist or is improperly formatted in: \"%s\"...";

    /**
     * Error message for when the number of tiles in the file doesn't meet
     * the expected and thus there would be null positions if it was compiled.
     */
    private static final String ERR_MISSING_TILES = "Number of Tiles in the "
            + "Map Layout doesn't meet the expected: [%s]; Actual: [%s]; "
            + "Latest Match: [%s]%n";

    /**
     * Wraps a regular expression file module.
     */
    public interface RegexModule {
        /**
         * @return Regex that will match this module.
         */
        Pattern getRegex();

        /**
         * @return Name of this module.
         */
        String name();
    }

    /**
     * Represents an aspect of the Rat game file.
     */
    protected enum Module implements RegexModule {
        /**
         * Properties aspect of the default file.
         */
        GAME_PROPERTIES(),

        /**
         * Item generator aspect of the default file.
         */
        ITEM_GENERATOR(),

        /**
         * Existing entity instances aspect of the default file.
         */
        ENTITY_INSTANCES(),

        /**
         * Tile map information of the default file.
         */
        MAP_LAYOUT();

        /**
         * Module content capture group.
         */
        private static final int CONTENT_GROUP = 1;

        /**
         * Regular expression to capture the groups held in the default file.
         */
        private final Pattern regex;

        Module() {
            final String base = "(?is)%s.*?\\{(.*?)}";
            this.regex = Pattern.compile(String.format(
                    base, name()
            ));
        }

        public Pattern getRegex() {
            return regex;
        }

        public int contentGroup() {
            return CONTENT_GROUP;
        }
    }

    /**
     * Absolute path to the default file.
     */
    private final String defaultFile;

    /**
     * All lines of the default file parsed into a string.
     */
    private final String content;

    /**
     * Parsed game properties.
     */
    private final GameProperties defaultProperties;

    /**
     * The Map data parsed from the default file.
     */
    private final Level level;

    /**
     * Item generator loaded from file.
     */
    private final RatItemGenerator defaultGenerator;

    /**
     * Maps all existing entities and their relevant positions in a game map.
     */
    private final HashMap<Entity, List<Coordinates<Integer>>> entityPositionMap;

    /**
     * Loads up a rat game file so that its content can be parsed.
     *
     * @param file The rat game file to load.
     * @throws NullPointerException          If the provided file is
     *                                       {@code null}.
     * @throws IOException                   If one occurs whilst attempting to
     *                                       read the provided Game file.
     * @throws MissingModuleException        If the provided file does not
     *                                       contain all the essential file
     *                                       modules.
     * @throws DuplicateModuleException      If there exists more than one of
     *                                       the essential modules.
     * @throws InvalidModuleContentException If any of the module content is
     *                                       set up improperly or missing.
     */
    public RatGameFile(final File file) throws IOException,
            RatGameFileException, UnknownSpriteEnumeration {
        // Base setup
        Objects.requireNonNull(file);
        this.defaultFile = file.getAbsolutePath();
        this.content = Files.lines(file.toPath()).collect(Collectors.joining());

        // Ensure module existence
        ensureModulePresence(Module.values());

        // Parse modules
        this.defaultProperties = new GameProperties(
                getModule(Module.GAME_PROPERTIES, this.content)
        );

        // Construct level
        getTileSoftMatches();
        this.level = new Level(
                defaultProperties.getRows(),
                defaultProperties.getColumns(),
                defaultFile
        );
        loadTiles();

        // Load item generator
        this.defaultGenerator = loadItemGenerator(this.content);

        // Load entities
        this.entityPositionMap = loadEntities(this.content);
    }

    /**
     * Loads from the provided content string all the of the entities and
     * their relevant positions.
     *
     * @param content The base content to parse the module from.
     * @return Parsed entities and their relevant positions.
     */
    private HashMap<Entity, List<Coordinates<Integer>>> loadEntities(
            final String content)
            throws ImproperlyFormattedArgs, InvalidArgsContent {

        final String moduleContent = getModule(
                Module.ENTITY_INSTANCES, content
        ).replaceAll("\\s", "");

        Matcher m = EntityLoader.SOFT_MATCH_REGEX.matcher(moduleContent);

        final HashMap<Entity, List<Coordinates<Integer>>> entityPosMap
                = new HashMap<>();

        while (m.find()) {
            final String whole = m.group();
            final String positions = m.group(EntityLoader.SOFT_MATCH_POS_GROUP);

            final Entity entity = EntityLoader.build(whole);
            final List<Coordinates<Integer>> occupied =
                    parsePositions(positions);

            entityPosMap.put(entity, occupied);
        }

        return entityPosMap;
    }

    /**
     * Loads from the provided positions set all the literal position values.
     *
     * @param pos The positions to parse.
     * @return Parsed positions.
     * @throws InvalidArgsContent If the content held within a Row or Column
     *                            exceeds the minimum or maximum for an Integer.
     */
    private List<Coordinates<Integer>> parsePositions(final String pos)
            throws InvalidArgsContent {
        final Pattern expected = Pattern.compile("(([0-9]+),([0-9]+))");
        final Matcher m = expected.matcher(pos);

        final List<Coordinates<Integer>> positions = new ArrayList<>();
        final int rowGroup = 2;
        final int colGroup = 3;
        while (m.find()) {
            try {
                final int row = Integer.parseInt(m.group(rowGroup));
                final int col = Integer.parseInt(m.group(colGroup));

                positions.add(new Coordinates<>(row, col));
            } catch (NumberFormatException e) {
                throw new InvalidArgsContent(pos);
            }
        }

        return positions;
    }

    /**
     * Loads the item generator from the string content.
     *
     * @param content Content to get the item generator from.
     * @return Newly constructed item generator.
     */
    private RatItemGenerator loadItemGenerator(final String content)
            throws ImproperlyFormattedArgs, InvalidArgsContent {

        final String moduleContent = getModule(Module.ITEM_GENERATOR,
                content).replaceAll("\\s", "");

        final Matcher m = ItemGeneratorLoader.SOFT_MATCH_REGEX.matcher(
                moduleContent
        );

        // Create inventory
        final RatItemGenerator inventory = new RatItemGenerator();
        while (m.find()) {
            final ItemGenerator<?> generator = ItemGeneratorLoader.build(
                    m.group()
            );
            inventory.addGenerator(generator);

        }

        return inventory;
    }

    /**
     * Loads from the {@link Module#MAP_LAYOUT} all the tiles that make up
     * the map.
     */
    private void loadTiles()
            throws InvalidModuleContentException, UnknownSpriteEnumeration {
        // This method can further improve error detection by parsing what is
        // there and then comparing it to what isn't. That way we can say,
        // [Tile row, col] is missing.
        final int expectedTileCount =
                defaultProperties.getRows() * defaultProperties.getColumns();
        final List<String> tiles = getTileSoftMatches();

        // Throw exception if not enough tiles
        if (tiles.size() != expectedTileCount) {
            throw new InvalidModuleContentException(String.format(
                    ERR_MISSING_TILES,
                    expectedTileCount,
                    tiles.size(),
                    tiles.size() > 0 ? tiles.get(tiles.size() - 1) : "N/A"
            ));

            // Create tile map
        } else {
            for (String raw : tiles) {
                final Tile t = TileLoader.buildTile(raw);
                level.setTile(t, t.getRow(), t.getCol());
            }
        }
    }

    /**
     * Searches through the Tile map section of the file for all soft matches.
     *
     * @return All soft matches found using the soft match regex.
     * @see TileLoader#isSoftmatch(String)
     */
    private List<String> getTileSoftMatches() {
        final Matcher m = TileLoader.SOFT_MATCH_REGEX.matcher(
                getModule(Module.MAP_LAYOUT, this.content)
                        .replaceAll("\\s", "")
        );
        final List<String> softMatches = new ArrayList<>();

        while (m.find()) {
            softMatches.add(m.group());
        }

        return softMatches;
    }

    /**
     * Checks to see if all the required modules exist for the game file.
     *
     * @param modules The modules to ensure existence for.
     * @throws MissingModuleException   If a module which is essential, does
     *                                  not exist.
     * @throws DuplicateModuleException If a module has a duplicate entry.
     */
    protected void ensureModulePresence(final RegexModule[] modules)
            throws MissingModuleException, DuplicateModuleException {

        // Ensure all essential file modules are present
        for (RegexModule module : modules) {
            final Matcher m = module.getRegex().matcher(content);

            // Doesn't exist?
            if (!m.find()) {
                throw new MissingModuleException(String.format(
                        ERR_MISSING_MODULE,
                        module.name(),
                        this.defaultFile
                ));

                // Exists more than once?
            } else if (m.find()) {
                throw new DuplicateModuleException(String.format(
                        ERR_DUPLICATE_MODULE,
                        module.name(),
                        this.defaultFile
                ));
            }
        }
    }

    /**
     * Gets the provided module from the file content used to construct this
     * file.
     *
     * @param module  The module to get.
     * @param content The content to get the module from.
     * @return Module content string with leading and trailing spaces deleted.
     */
    protected String getModule(final Module module,
                               final String content) {
        final Matcher m = module.getRegex().matcher(content);

        // Should always exist
        if (!m.find()) {
            throw new AssertionError(module.name());
        }

        return m.group().trim();
    }

    /**
     * Using regular expression capture the content held within the module at
     * the provided capture group.
     *
     * @param module       The module to obtain.
     * @param content      The content to obtain the module from.
     * @param captureGroup The specific module content to get.
     * @return Module content string as is. Unformatted.
     * @throws AssertionError            If the module does not exist in the
     *                                   provided content.
     * @throws IndexOutOfBoundsException If the capture group doesn't exist.
     */
    protected String getModuleContent(final RegexModule module,
                                      final String content,
                                      final int captureGroup) {
        final Matcher m = module.getRegex().matcher(content);

        if (!m.find()) {
            throw new AssertionError();
        }

        return m.group(captureGroup);
    }

    /**
     * @return Absolute path to the default file used to construct this.
     */
    public String getDefaultFile() {
        return defaultFile;
    }

    /**
     * @return The default file properties.
     */
    public GameProperties getDefaultProperties() {
        return defaultProperties;
    }

    /**
     * @return The default tile map.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @return The default item generator.
     */
    public RatItemGenerator getDefaultGenerator() {
        return defaultGenerator;
    }

    /**
     * @return The entity position mapping.
     */
    public HashMap<Entity, List<Coordinates<Integer>>> getEntityPositionMap() {
        return entityPositionMap;
    }
}
