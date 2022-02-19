package game.entity.subclass.bomb;

import game.RatGame;
import game.classinfo.tags.BlackListed;
import game.classinfo.tags.DisplaySpriteResource;
import game.classinfo.tags.TargetConstructor;
import game.classinfo.tags.WritableField;
import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.entity.Entity;
import game.entity.Item;
import game.event.impl.entity.specific.general.EntityDeOccupyTileEvent;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityOccupyTileEvent;
import game.event.impl.entity.specific.general.GenericAudioEvent;
import game.event.impl.entity.specific.general.SpriteChangeEvent;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.base.grass.Grass;
import game.tile.base.tunnel.Tunnel;
import gui.game.EventAudio.GameAudio;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Bomb Game Item object that once placed into the game will countdown on
 * each update until finally exploding; killing any and all Entities in
 * a given radius upon exploding.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class Bomb extends Item {

    /**
     * Tiles that this will never exist on. Primarily the main sprite will
     * never exist on this.
     */
    @BlackListed
    private static final Class<?>[] BLACK_LISTED_TILES = {
            Grass.class,
            Tunnel.class
    };

    /**
     * Time in milliseconds all bombs will explode after.
     */
    private static final int BOMB_TIMER = 5_000;

    /**
     * 1st stage of Bomb explosion (last before explosion).
     */
    private static final int BOMB_TIMER_STAGE_1 = 1_000;

    /**
     * 2nd stage of Bomb explosion (last before explosion).
     */
    private static final int BOMB_TIMER_STAGE_2 = 2_000;

    /**
     * 3rd stage of Bomb explosion (last before explosion).
     */
    private static final int BOMB_TIMER_STAGE_3 = 3_000;

    /**
     * 4th stage of Bomb explosion (last before explosion).
     */
    private static final int BOMB_TIMER_STAGE_4 = 4_000;

    /**
     * Time in milliseconds when all bombs explode.
     */
    private static final int BOMB_EXPLOSION_TIME = 0;

    /**
     * Bomb explode image resource.
     */
    private static final URL BOMB_EXPLODE_IMAGE
            = Bomb.class.getResource("assets/Explosion.png");

    /**
     * Bomb image resource.
     */
    private static final URL BOMB_IMAGE_1
            = Bomb.class.getResource("assets/Bomb_1.png");

    /**
     * Bomb image resource.
     */
    private static final URL BOMB_IMAGE_2
            = Bomb.class.getResource("assets/Bomb_2.png");

    /**
     * Bomb image resource.
     */
    private static final URL BOMB_IMAGE_3
            = Bomb.class.getResource("assets/Bomb_3.png");

    /**
     * Bomb image resource.
     */
    private static final URL BOMB_IMAGE_4
            = Bomb.class.getResource("assets/Bomb_4.png");

    /**
     * Bomb image resource.
     */
    @DisplaySpriteResource
    private static final URL BOMB_IMAGE
            = Bomb.class.getResource("assets/Bomb.png");

    /**
     * Bomb explosion sound.
     */
    private static final URL EXPLOSION_SOUND
            = Bomb.class.getResource("assets/explosion.mp3");

    /**
     * Current time before the time explodes.
     */
    @WritableField(name = "Time until Explosion",
            defaultValue = "" + BOMB_TIMER)
    private int currentTime;

    /**
     * Bomb build String list - first argument.
     */
    private static final int ROW_ARG_INDEX = 0;

    /**
     * Bomb build String list - second argument.
     */
    private static final int COL_ARG_INDEX = 1;

    /**
     * Bomb build String list - third argument.
     */
    private static final int HEALTH_ARG_INDEX = 2;

    /**
     * Bomb build String list - fourth argument.
     */
    private static final int CURRENT_TIME_ARG_INDEX = 3;

    /**
     * Time in milliseconds for the entity removal
     * thread to sleep after creation.
     */
    private static final int REMOVAL_THREAD_SLEEP_TIME = 100;

    /**
     * Builds a Bomb object from the provided args string.
     *
     * @param args Arguments used to build a bomb.
     * @return Newly constructed Bomb.
     * @throws ImproperlyFormattedArgs if the String can not be parsed.
     * @throws InvalidArgsContent      if the arguments are not formatted
     *                                 correctly.
     */
    public static Bomb build(final String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final int expectedArgsLength = 4;

        if (args.length != expectedArgsLength) {
            throw new ImproperlyFormattedArgs(Arrays.deepToString(args));
        }

        try {
            final int row = Integer.parseInt(args[ROW_ARG_INDEX]);
            final int col = Integer.parseInt(args[COL_ARG_INDEX]);
            final int health = Integer.parseInt(args[HEALTH_ARG_INDEX]);
            final int currentTime = Integer.parseInt(
                    args[CURRENT_TIME_ARG_INDEX]);

            return new Bomb(row, col, health, currentTime);
        } catch (Exception e) {
            throw new InvalidArgsContent(Arrays.deepToString(args));
        }
    }

    /**
     * Construct an Entity from the base starting Row and Column.
     *
     * @param initRow Row in a 2D Array. A[ROW][COL]
     * @param initCol Col in a 2D Array. A[ROW][COL]
     */
    @TargetConstructor
    public Bomb(final int initRow,
                final int initCol) {
        super(initRow, initCol);
        currentTime = BOMB_TIMER;
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow Row in a 2D Array. A[ROW][COL]
     * @param initialCol Col in a 2D Array. A[ROW][COL]
     * @param curHealth  Current health of the Entity.
     */
    public Bomb(final int initialRow,
                final int initialCol,
                final int curHealth) {
        super(initialRow, initialCol, curHealth);
        currentTime = BOMB_TIMER;
    }

    /**
     * Construct an Entity from the base starting x, y, and health values.
     *
     * @param initialRow         Row in a 2D Array. A[ROW][COL]
     * @param initialCol         Col in a 2D Array. A[ROW][COL]
     * @param curHealth          Current health of the Entity.
     * @param timeUntilExplosion Current time remaining until explosion of the
     *                           Entity.
     */
    public Bomb(final int initialRow,
                final int initialCol,
                final int curHealth,
                final int timeUntilExplosion) {
        super(initialRow, initialCol, curHealth);
        this.currentTime = timeUntilExplosion;
    }

    /**
     * Returns the current bomb timer value.
     *
     * @return currentTime Current timer value (time left for the bomb to
     * *              explode).
     */
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * Sets the current time value to the specified argument.
     *
     * @param timeUntilExplosion Current timer value (time left for the bomb to
     *                           explode).
     */
    public void setCurrentTime(final int timeUntilExplosion) {
        this.currentTime = timeUntilExplosion;
    }

    /**
     * Place where this entity can be updated and, do something once provided
     * some context objects.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this entity.
     */
    @Override
    public void update(final ContextualMap contextMap,
                       final RatGame ratGame) {
        setCurrentTime(getCurrentTime() - ratGame.getUpdateTimeFrame());
        URL bombImage;
        if (getCurrentTime() <= BOMB_TIMER_STAGE_1) {
            bombImage = BOMB_IMAGE_1;
        } else if (getCurrentTime() <= BOMB_TIMER_STAGE_2) {
            bombImage = BOMB_IMAGE_2;
        } else if (getCurrentTime() <= BOMB_TIMER_STAGE_3) {
            bombImage = BOMB_IMAGE_3;
        } else if (getCurrentTime() <= BOMB_TIMER_STAGE_4) {
            bombImage = BOMB_IMAGE_4;
        } else {
            bombImage = BOMB_IMAGE_4;
        }

        this.fireEvent(new SpriteChangeEvent(
                this,
                0,
                bombImage
        ));

        if (getCurrentTime() <= BOMB_EXPLOSION_TIME) {
            explode(contextMap, ratGame);
        }
    }

    /**
     * Explode the bomb, creating a new explosion entity on every reachable
     * tile within a cardinal direction. All other entities occupying the
     * same tiles will be killed.
     *
     * @param contextMap The map that this entity may exist on.
     * @param ratGame    The game that updated this entity.
     */
    private void explode(final ContextualMap contextMap,
                         final RatGame ratGame) {

        final List<TileData> tiles = new ArrayList<>();

        final CardinalDirection[] directions = {
                CardinalDirection.NORTH,
                CardinalDirection.EAST,
                CardinalDirection.SOUTH,
                CardinalDirection.WEST
        };

        for (CardinalDirection direction : directions) {
            tiles.addAll(contextMap.getTilesInDirection(
                    direction,
                    contextMap.getOriginTile(this),
                    Grass.class));
        }

        this.fireEvent(new SpriteChangeEvent(
                this,
                0,
                BOMB_EXPLODE_IMAGE
        ));

        this.fireEvent(new GenericAudioEvent(
                this,
                GameAudio.BOMB_EXPLOSION.getResource()
        ));

        //Instantiate explosion entity for each tile reached by the explosion
        tiles.forEach(tile -> {
            this.fireEvent(new EntityOccupyTileEvent(
                    this,
                    tile.getRow(),
                    tile.getCol(),
                    0,
                    BOMB_EXPLODE_IMAGE,
                    null));

            //Kill all entities on the tile
            for (Entity entity : tile.getEntities()) {
                entity.kill();
            }
        });

        //Kill entities on bomb tile
        Entity[] originEntities = contextMap.getTileDataAt(this.getRow(),
                this.getCol()).getEntities();
        for (Entity entity : originEntities) {
            //Don't kill the bomb itself, only other entities on the same tile.
            if (!entity.equals(this)) {
                entity.kill();
            }
        }

        var thread = new Thread(() -> {
            try {
                Thread.sleep(REMOVAL_THREAD_SLEEP_TIME);
                tiles.forEach(tile -> {
                    this.fireEvent(new EntityDeOccupyTileEvent(
                            this,
                            tile.getRow(),
                            tile.getCol()));
                });

                this.kill();

                this.fireEvent(new EntityDeathEvent(
                        this,
                        BOMB_EXPLODE_IMAGE,
                        null));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        thread.start();

    }

    /**
     * Convenience method to kill this Entity.
     */
    @Override
    public void kill() {
        super.kill();
        this.fireEvent(new EntityDeathEvent(
                this,
                null,
                null
        ));
    }

    /**
     * Returns the display sprite resource for this item.
     *
     * @return Resource attached to an image file to display.
     */
    @Override
    public URL getDisplaySprite() {
        return BOMB_IMAGE;
    }

    /**
     * Builds the Entity to a String that can be saved to a File; all
     * parameters to construct the current state of the entity are required.
     *
     * @param contextMap The context map which contains extra info that may
     *                   not be stored directly in the Entity class.
     */
    @Override
    public String buildToString(final ContextualMap contextMap) {
        return String.format(
                "[Bomb, [%s,%s,%s,%s], []]",
                getRow(),
                getCol(),
                getHealth(),
                getCurrentTime()
        );
    }
}
