package game.event.impl.entity.specific.player;

import game.event.GameEvent;
import game.event.base.AudioEvent;
import game.player.Player;

import java.net.URL;

/**
 * Event wraps when a player gains some score from doing something.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class ScoreUpdateEvent extends GameEvent<Player> implements AudioEvent {

    /**
     * The score that the player gained.
     */
    private final int scoreGain;

    /**
     * The audio clip to play with this event.
     */
    private final URL audioClip;

    /**
     * Constructs a game event from a target author.
     *
     * @param author The author of the event.
     * @throws NullPointerException If the author is a {@code null}.
     */
    public ScoreUpdateEvent(final Player author,
                            final int scoreGain,
                            final URL audioClip) {
        super(author);
        this.scoreGain = scoreGain;
        this.audioClip = audioClip;
    }

    /**
     * @return The amount of score the player gained.
     */
    public int getScoreGain() {
        return scoreGain;
    }

    /**
     * @return Resource attached to an audio clip that should be played
     * when this event happens.
     */
    @Override
    public URL getAudioClip() {
        return audioClip;
    }
}
