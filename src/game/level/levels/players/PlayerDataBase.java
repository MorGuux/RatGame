package game.level.levels.players;

import game.level.levels.RatGameLevel;
import game.level.reader.exception.InvalidArgsContent;
import game.player.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class acts as a read write controller to an underlying resource. Players
 * can be added but not removed, however writes are only Truncate -> commit,
 * so it isn't needed to have an individual method for deletion.
 *
 * @author -Ry
 * @version 0.3
 * Copyright: N/A
 */
public class PlayerDataBase {

    /**
     * Resource location for the known players.
     */
    private static final URL KNOWN_PLAYERS_DB =
            PlayerDataBase.class.getResource("KnownPlayers.pdb");

    /**
     * Path from the src root to the known players database.
     */
    private static final String PLAYER_DB_SRC_PATH
            = "src/game/level/levels/players/KnownPlayers.pdb";

    /**
     * Regex used to rip modules out of the player database so that players
     * can be loaded.
     */
    private static final Pattern PLAYER_DATA_MODULE_REGEX
            = Pattern.compile("(?is)PLAYER_DATA\\s*\\{(.*?)}");

    /**
     * The relevant capture group used when parsing primary data.
     */
    private static final int RELEVANT_CAPTURE_GROUP = 1;

    /**
     * Regex to rip the players name out from a module.
     */
    private static final Pattern PLAYER_NAME_TAG
            = Pattern.compile("(?i)NAME:\\s*(.*?)\\s*;");

    /**
     * Regex to rip the levels unlocked out from a module of a player.
     */
    private static final Pattern LEVELS_UNLOCKED_TAG
            = Pattern.compile("(?i)LEVELS_UNLOCKED:\\s*\\[(.*?)];");

    /**
     * Template format for a new entry in the Player database.
     */
    private static final String ENTRY_TEMPLATE = "%nPLAYER_DATA {%n    "
            + "NAME: %s;%n    LEVELS_UNLOCKED: %s;%n}";

    /**
     * Error case for when the known database can't be loaded due to it being
     * missing.
     */
    private static final String ERR_DB_NULL = "Error Known players database "
            + "is null!";

    /**
     * File content parsed from file.
     */
    private String rawContent;

    /**
     * List of all the known players.
     */
    private final ObservableList<Player> players;

    /**
     * Constructs the player database loading all currently known players
     * ready to be accessed.
     *
     * @throws IOException        If one occurs whilst reading the database.
     * @throws URISyntaxException If players database has become malformed.
     * @throws InvalidArgsContent If any of the data in the database has
     *                            become malformed.
     */
    public PlayerDataBase()
            throws IOException,
            URISyntaxException,
            InvalidArgsContent {

        if (KNOWN_PLAYERS_DB == null) {
            throw new NullPointerException(ERR_DB_NULL);
        }

        // Load file content
        rawContent = Files.lines(Path.of(KNOWN_PLAYERS_DB.toURI()))
                .collect(Collectors.joining(System.lineSeparator()));

        // Load players
        players = FXCollections.synchronizedObservableList(
                loadPlayers(this.rawContent)
        );
    }

    /**
     * Loads all players from the provided known players database string.
     *
     * @param content The full database content to load from.
     * @return All players parsed from the database.
     */
    private ObservableList<Player> loadPlayers(final String content)
            throws InvalidArgsContent {
        final ObservableList<Player> players
                = FXCollections.observableArrayList();

        final Matcher moduleMatcher
                = PLAYER_DATA_MODULE_REGEX.matcher(content);

        while (moduleMatcher.find()) {
            final String moduleInfo =
                    moduleMatcher.group(RELEVANT_CAPTURE_GROUP);

            players.add(loadPlayer(moduleInfo));
        }

        return players;
    }

    /**
     * Loads a player from the provided module.
     *
     * @param moduleInfo A module from the player database that needs to be
     *                   parsed into a player.
     * @return Parsed player.
     * @throws InvalidArgsContent If any of the data is malformed.
     */
    private Player loadPlayer(final String moduleInfo)
            throws InvalidArgsContent {

        final Matcher m = PLAYER_NAME_TAG.matcher(moduleInfo);

        if (m.find()) {
            final String playerName = m.group(RELEVANT_CAPTURE_GROUP);
            final List<RatGameLevel> levelsUnlocked
                    = loadUnlockedLevels(moduleInfo);

            return new Player(playerName, levelsUnlocked);

            // Player was malformed
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Parses the levels unlocked for a player in the players' database.
     *
     * @param moduleInfo The specific player data module to parse.
     * @return All levels unlocked for the target player.
     * @throws InvalidArgsContent If the data held within the module is
     *                            malformed.
     */
    private List<RatGameLevel> loadUnlockedLevels(final String moduleInfo)
            throws InvalidArgsContent {
        final Matcher m = LEVELS_UNLOCKED_TAG.matcher(moduleInfo);
        final List<RatGameLevel> levelsUnlocked = new ArrayList<>();

        if (m.find()) {
            final String raw = m.group(RELEVANT_CAPTURE_GROUP);
            final String argDelimiter = ",";

            for (String level : raw.split(argDelimiter)) {
                try {
                    final RatGameLevel parsedLevel =
                            RatGameLevel.getLevelFromName(level);
                    levelsUnlocked.add(parsedLevel);
                } catch (Exception e) {
                    throw new InvalidArgsContent(
                            raw
                    );
                }
            }
        } else {
            throw new IllegalStateException();
        }

        return levelsUnlocked;
    }

    /**
     * @return All known players in the player database.
     */
    public ObservableList<Player> getPlayers() {
        return this.players;
    }

    /**
     * Checks to see if the provided player name exists in the known players
     * database.
     *
     * @param playerName The name of the player to check for existence.
     * @return {@code true} if the player exists in the database.
     */
    public boolean isPlayerPresent(final String playerName) {
        return this.players.contains(new Player(playerName));
    }

    /**
     * @param playerName The name of the player to get.
     * @return The player to add.
     * @throws IndexOutOfBoundsException If the player does not exist in the
     *                                   players array.
     */
    public Player getPlayer(final String playerName) {
        return this.players.get(this.players.indexOf(new Player(playerName)));
    }

    /**
     * Commits the provided player to the Players database. If the provided
     * player is a known player then the previous data for the player is set
     * to the provided and the old is lost. If the player is a new player
     * then a new entry is added.
     *
     * @param p The player to add.
     * @throws IOException If one occurs whilst writing to the database.
     */
    public void commitPlayer(final Player p) throws IOException {
        // Updating an existing player
        if (this.players.contains(p)) {
            final Player originalPlayer =
                    this.players.get(this.players.indexOf(p));
            this.rawContent = this.rawContent.replaceAll(
                    Pattern.quote(buildPlayerString(originalPlayer)),
                    buildPlayerString(p)
            );
            // Remove the player if they exist (have been loaded)
            this.players.removeIf((a) -> a.equals(p));

            // Adding a new player
        } else {
            this.rawContent = String.format("%s%n%s%n",
                    this.rawContent,
                    buildPlayerString(p)
            );
        }
        this.players.add(p);

        writeToFile();
    }

    /**
     * Internal write function that handles writing to the player database
     * the content held within the raw string.
     */
    private synchronized void writeToFile() throws IOException {
        Files.writeString(
                Path.of(PLAYER_DB_SRC_PATH),
                this.rawContent,
                StandardCharsets.UTF_8
        );
    }

    /**
     * Constructs an entry for a player formatted in the required way.
     *
     * @param p The player to build into a string.
     * @return Formatted string.
     */
    private String buildPlayerString(final Player p) {
        return String.format(ENTRY_TEMPLATE, p.getPlayerName(),
                Arrays.deepToString(p.getLevelsUnlocked()));
    }
}
