package game.tile.exception;

/**
 * Represents when a Sprite enumeration type is unknown or illegally formatted.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class UnknownSpriteEnumeration extends Exception {

    /**
     * Construct the exception with the provided error message.
     * @param s The error message/reason for the exception.
     */
    public UnknownSpriteEnumeration(final String s) {
        super(s);
    }
}
