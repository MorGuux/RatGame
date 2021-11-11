package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MOTD {

    final static String MOTD_URL = "http://cswebcat.swansea.ac.uk/puzzle";

    private static String getMOTDPuzzle() throws IOException {
        return getRequest(MOTD_URL);
    }

    private static String getRequest(String URL) throws IOException {
        //Construct GET request
        URL url = new URL(URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        //Get response
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine = in.readLine();
        in.close();

        return inputLine;
    }

    private static String solvePuzzle(String puzzle) {
        StringBuilder solvedPuzzle = new StringBuilder();
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

    static char shiftCharacter(char character, int shift) {
        char c = (char) (character + shift);
        if (c > 'Z')
            c = (char) (character - (26 - shift));
        else if (c < 'A') {
            c = (char) (character + (26 + shift));
        } else {
            c = (char) (character + shift);
        }
        return Character.toUpperCase(c);
    }

    /**
     * Get the message of the day from http://cswebcat.swan.ac.uk/
     *
     * @return the message of the day
     * @throws IOException
     */
    public static String getMOTD() throws IOException {
        String solvedPuzzle = solvePuzzle(getMOTDPuzzle());
        return getRequest("http://cswebcat.swansea.ac.uk/message?solution=" + solvedPuzzle);
    }

}
