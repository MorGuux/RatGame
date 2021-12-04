package game.level.levels.players;

import game.contextmap.handler.MovementHandler;
import game.level.levels.RatGameLevel;
import game.level.reader.exception.InvalidArgsContent;
import game.player.Player;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class PlayerDataBase {

    /**
     *
     */
    private static final URL KNOWN_PLAYERS_DB =
            PlayerDataBase.class.getResource("KnownPlayers.pdb");

    /**
     *
     */
    private static final Pattern PLAYER_DATA_MODULE_REGEX
            = Pattern.compile("(?is)PLAYER_DATA\\s*\\{(.*?)}");

    /**
     *
     */
    private static final int RELEVANT_CAPTURE_GROUP = 1;

    /**
     *
     */
    private static final Pattern PLAYER_NAME_TAG
            = Pattern.compile("(?i)NAME:\\s*(.*?)\\s*;");

    /**
     *
     */
    private static final Pattern LEVELS_UNLOCKED_TAG
            = Pattern.compile("(?i)LEVELS_UNLOCKED:\\s*\\[(.*?)];");

    /**
     *
     */
    private static final String ENTRY_TEMPLATE = "%nPLAYER_DATA {%n    "
            + "NAME: %s;%n    LEVELS_UNLOCKED: %s;%n}";

    /**
     *
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
    private final List<Player> players;

    /**
     *
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
        players = loadPlayers(this.rawContent);
    }

    /**
     * @param content
     * @return
     */
    private List<Player> loadPlayers(final String content)
            throws InvalidArgsContent {
        final List<Player> players = new ArrayList<>();

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
     * @param moduleInfo
     * @return
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
     * @param moduleInfo
     * @return
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
    public List<Player> getPlayers() {
        return this.players;
    }

    public void commitPlayer(final Player p) {
        // Updating an existing player
        if (this.players.contains(p)) {
            this.rawContent = this.rawContent.replaceAll(
                    this.rawContent,
                    buildPlayerString(p)
            );

            // Adding a new player
        } else {
            this.rawContent = String.format("%s%n%s%n",
                    this.rawContent,
                    String.format(ENTRY_TEMPLATE, p.getPlayerName(),
                            Arrays.deepToString(p.getLevelsUnlocked()))
            );
        }

        System.out.println(this.rawContent);
    }

    /**
     *
     * @param p
     * @return
     */
    private String buildPlayerString(final Player p) {
        return String.format(ENTRY_TEMPLATE, p.getPlayerName(),
                Arrays.deepToString(p.getLevelsUnlocked()));
    }

    public static void main(String[] args)
            throws IOException, URISyntaxException, InvalidArgsContent {
        final PlayerDataBase dataBase = new PlayerDataBase();

        Player p = new Player("ASD", MovementHandler.getAsList(RatGameLevel.LEVEL_ONE));

        dataBase.commitPlayer(p);

    }
}
