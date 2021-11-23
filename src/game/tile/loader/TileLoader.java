package game.tile.loader;

import game.tile.Tile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.MalformedParametersException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class designed to load from Strings {@link Tile} Objects. Also
 * provides a fine level of Error information for malformed strings.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public final class TileLoader {

    /**
     * Hide constructor.
     */
    private TileLoader() {
    }

    /**
     * Builds a tile from the provided String if it is possible to do so.
     *
     * @param raw String to parse into a Tile object.
     * @return Parsed tile from the provided args string.
     * @throws NullPointerException         If the string args are null.
     * @throws MalformedParametersException If the string does not even pass
     *                                      as a soft match.
     * @throws IllegalStateException        If the string is a soft match but
     *                                      something else was wrong with the
     *                                      string.
     */
    public static Tile buildTile(final String raw) {
        Objects.requireNonNull(raw);
        final String safe = raw.replaceAll("\\s", "");
        if (isSoftmatch(safe)) {

            // Deduce the soft match to a hard match
            for (TileRegex tr : TileRegex.values()) {
                if (tr.casAssertThis(safe)) {
                    return tr.build(safe);
                }
            }

            // At this point, if no match has been found then its malformed
            throw new IllegalStateException(deduceIssue(safe));


            // String just isn't proper
        } else {
            throw new MalformedParametersException();
        }
    }

    /**
     * Attempts to deduce the issue/problem the file has.
     *
     * @param safe Bad args that won't compile.
     * @return Error message about why it failed.
     */
    private static String deduceIssue(final String safe) {
        final TileRegex[] values = TileRegex.values();
        final String base = "\\[%s,.*?]]";

        // Detect issue (the build throws precise errors)
        for (TileRegex v : values) {
            if (safe.matches(String.format(base, v.name()))) {
                try {
                    v.build(safe);
                } catch (Exception e) {
                    return e.getMessage();
                }
            }
        }

        return "Undecipherable issue for: " + safe;
    }

    /**
     * Checks if the provided string is a soft match for the expected. That
     * means that it is of the style, but may not actually be parseable.
     *
     * @param s String to check.
     * @return {@code true} if the provided String is a soft match for Tile
     * args construction. Otherwise, if not {@code false} is returned.
     */
    public static boolean isSoftmatch(final String s) {
        // Matches generically: [A,[B_A,0,0]]
        return s.matches("(?im)\\[[a-z]+,\\[[a-z_]+,[0-9]+,[0-9]+]]");
    }

    // todo Remove once testing is finished.
    public static void main(String[] args) throws IOException {
        final Pattern map = Pattern.compile("(?is)MAP_LAYOUT.*?\\{(.*?)}");
        final Pattern tile = Pattern.compile("(?im)\\[[a-z]+,\\[[a-z_]+,[0-9]+,[0-9]+]]");
        final File f = new File("src/game/tile/loader/levelOne.txt");

        final String s = Files.lines(f.toPath()).collect(Collectors.joining());

        Matcher m = map.matcher(s);

        if (m.find()) {
            final String mapContent = m.group(1).replaceAll("\\s", "");
            m = tile.matcher(mapContent);

            int totalErrors = 0;
            while (m.find()) {
                try {
                    System.out.println(buildTile(m.group()).getClass().getSimpleName());

                } catch (Exception e) {
                    ++totalErrors;
                    System.out.println(e.getMessage());
                }
            }
            System.out.printf("Finished with %s issues...", totalErrors);
        }
    }
}
