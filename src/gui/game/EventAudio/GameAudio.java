package gui.game.EventAudio;

import java.net.URL;

/**
 * Game audio enumeration wrapper that wraps all the possible game audio that
 * our game will use.
 *
 * @author Maksim, Ry
 * @version 0.1
 * Copyright: N/A
 */
public enum GameAudio {

    /**
     * Sound effect for when a bomb explodes.
     */
    BOMB_EXPLOSION(getResource("BombExplosion.wav")),

    /**
     * Sound effect for when a button is pressed.
     */
    BUTTON_PRESS(getResource("ButtonPress.wav")),

    /**
     * SFX for when gas spawns.
     */
    GAS(getResource("Gas.wav")),

    /**
     * SFX for irradiation.
     */
    IRRADIATE(getResource("Irradiate.wav")),

    /**
     * SFX For entity collision with a no-entry sign.
     */
    NO_ENTRY_COLLISION(getResource("NoEntrySignCollision.wav")),

    /**
     * SFX for when an item is placed into the game.
     */
    PLACE_ITEM(getResource("PlaceItem.wav")),

    /**
     * SFX for when a rat dies.
     */
    RAT_DEATH(getResource("RatDeath.wav")),

    /**
     * SFX For when rats have a quick shag.
     */
    RAT_SEX(getResource("RatSex.wav"));

    /**
     * The URL Resource of the audio file ready to be played whenever.
     */
    private final URL resource;

    /**
     * @param audioResource The URL of the audio resource for this ordinal.
     */
    GameAudio(final URL audioResource) {
        this.resource = audioResource;
    }

    /**
     * @return URL Resource of the target audio to play.
     */
    public URL getResource() {
        return resource;
    }

    /**
     * Convenience method for loading a resource from the game audio class.
     *
     * @param s The name of the resource to load.
     * @return The resource that was loaded.
     */
    private static URL getResource(final String s) {
        return GameAudio.class.getResource(s);
    }
}
