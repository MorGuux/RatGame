package game.motd;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Message of the Day Client which allows the polling of Webcat
 * to produce deciphered messages of the day.
 *
 * @author Morgan Gardener
 * @version 0.1
 */
public class MOTDClient {

    /**
     * Error message for malformed URI's.
     */
    private static final String ERR_INVALID_URI
            = "Host: [%s] could not be resolved...";

    /**
     * Host request target.
     */
    private static final String HOST
            = "http://cswebcat.swansea.ac.uk/puzzle";

    /**
     * Host result target.
     */
    private static final String REQUEST_TARGET
            = "http://cswebcat.swansea.ac.uk/message?solution=%s";

    /**
     * HTTP Request client.
     */
    private final HttpClient client;

    /**
     * Current Message of the Day.
     */
    private String currentMotd;

    /**
     * Current puzzle in its un-deciphered state.
     */
    private String currentPuzzle;

    /**
     * Used internally to discern if there are new messages to decipher.
     */
    private boolean hasNewMessage;


    /**
     * Constructs a Message of the Day Client.
     */
    public MOTDClient() {
        client = HttpClient.newHttpClient();
        this.currentMotd = "";
        this.currentPuzzle = "";
        this.hasNewMessage = false;
    }

    /**
     * Gets either a new, or old message. Depending on whether the old is
     * still 'new' in that there is no 'newer' message.
     * <p>
     * Note, that this method should always be preceded by
     * {@link #hasNewMessage()}.
     *
     * @return Current message of the day.
     */
    public String getMessage() {
        if (hasNewMessage) {
            try {
                this.currentMotd = parseMessage(solvePuzzle(currentPuzzle));

                // If error occurs return the previous motd
            } catch (final Exception e) {
                return currentMotd;
            }
            hasNewMessage = false;
        }
        return currentMotd;
    }

    /**
     * Gets a message of the day from the deciphered puzzle text.
     *
     * @param s Deciphered puzzle text.
     * @return Message of the day.
     * @throws IOException          If any occur whilst sending/reading the HTTP
     *                              Target.
     * @throws InterruptedException If the current thread is interrupted before
     *                              the server responds.
     */
    private String parseMessage(final String s)
            throws IOException, InterruptedException {
        final URI uri = buildDecipheredTarget(s);

        return makeGetRequest(uri).body();
    }

    /**
     * Solves a Puzzle from Webcat and returns the solved string.
     *
     * @param puzzle The puzzle to solve.
     * @return Solved puzzle string.
     */
    private String solvePuzzle(final String puzzle) {
        final StringBuilder solvedPuzzle = new StringBuilder();
        for (int i = 0; i < puzzle.length(); i++) {
            char c;
            if (i % 2 == 0) {
                c = shiftCharacter(puzzle.charAt(i), -(i + 1));
            } else {
                c = shiftCharacter(puzzle.charAt(i), i + 1);
            }

            solvedPuzzle.append(c);
        }
        solvedPuzzle.append("CS-230");

        solvedPuzzle.insert(0, solvedPuzzle.length());
        return solvedPuzzle.toString();
    }

    /**
     * Single character decipher method which shifts a characters position
     * conditionally.
     *
     * @param character Character to decipher/shift.
     * @param shift     Shift base value.
     * @return Deciphered character.
     */
    private char shiftCharacter(final char character,
                                final int shift) {
        final int charSize = 26;
        char c = (char) (character + shift);
        if (c > 'Z') {
            c = (char) (character - (charSize - shift));

        } else if (c < 'A') {
            c = (char) (character + (charSize + shift));

        } else {
            c = (char) (character + shift);
        }

        return Character.toUpperCase(c);
    }

    /**
     * @return {@code true} if there is a newer message than the one currently
     * store. Otherwise, if there are no new messages, or if there are
     * IOExceptions then {@code false} is returned.
     */
    public boolean hasNewMessage() {
        try {
            final String message = makeGetRequest(getHostAsUri()).body();

            if (this.currentPuzzle.equals(message)) {
                return false;

                // Update state and return
            } else {
                this.currentPuzzle = message;
                this.hasNewMessage = true;
                return true;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * Makes a get request to the provided target URI and returns the response.
     *
     * @param uri Target to produce the GET Request on.
     * @return HTTP Response from the Get request.
     * @throws IOException          If any occur whilst sending/receiving data.
     * @throws InterruptedException If the client the thread is interrupted
     *                              whilst waiting for a response.
     */
    private HttpResponse<String> makeGetRequest(final URI uri)
            throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * @return {@link #HOST} parsed into a URI.
     */
    private URI getHostAsUri() {
        try {
            return new URL(HOST).toURI();

            // This should never happen
        } catch (URISyntaxException
                | MalformedURLException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException(String.format(
                ERR_INVALID_URI, HOST
        ));
    }

    /**
     * Builds a deciphered target URI for the true deciphered message.
     *
     * @param decipheredText Deciphered message of the day.
     * @return Deciphered target ({@link #REQUEST_TARGET}) URI.
     */
    private URI buildDecipheredTarget(final String decipheredText) {
        final String s = String.format(REQUEST_TARGET, decipheredText);

        try {
            return new URL(s).toURI();
        } catch (URISyntaxException
                | MalformedURLException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException(String.format(
                ERR_INVALID_URI, s
        ));
    }
}
