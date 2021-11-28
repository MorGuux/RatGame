package game.entity.loader;

import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.entity.Entity;
import game.entity.subclass.bomb.Bomb;
import game.entity.subclass.deathRat.DeathRat;
import game.entity.subclass.femaleSexChange.FemaleSexChange;
import game.entity.subclass.gas.Gas;
import game.entity.subclass.maleSexChange.MaleSexChange;
import game.entity.subclass.noentry.NoEntry;
import game.entity.subclass.poison.Poison;
import game.entity.subclass.rat.Rat;
import game.entity.subclass.sterilisation.Sterilisation;
import game.level.reader.RatGameFile;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;
import game.tile.Tile;
import game.tile.base.grass.Grass;
import game.tile.base.grass.GrassSprite;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public final class EntityLoader {
    // todo finish commenting

    /**
     * Matches strings of the expected format but does not guarantee that data
     * held within is actually proper.
     * <p>
     * Relevant capture groups are:
     * <ol>
     *     <li>Entity Build Args</li>
     *     <li>Entity Occupied Positions</li>
     * </ol>
     */
    public static final Pattern SOFT_MATCH_REGEX
            = Pattern.compile("\\[[a-zA-Z_]+,\\[(.*?)],\\[(.*?)]]");

    /**
     * Group captures the build args to construct the target entity of the
     * String.
     */
    public static final int SOFT_MATCH_ARGS_GROUP = 1;

    /**
     * Group captures the Positions that the entity should occupy.
     */
    public static final int SOFT_MATCH_POS_GROUP = 2;

    /**
     * Allows the construction of any Entity from an args string.
     *
     * @author -Ry
     * @version 0.1
     * Copyright: N/A
     */
    private interface EntityFactory {
        Entity create(String[] args)
                throws InvalidArgsContent, ImproperlyFormattedArgs;
    }

    /**
     * Enumeration wraps all known Entities and their construction methods.
     * This only provides a soft matching system where it forces the correct
     * format but not the correct data that would be enforced else where.
     *
     * @author -Ry
     * @version 0.1
     * Copyright: N/A
     */
    private enum ConstructableEntity implements RatGameFile.RegexModule {

        /**
         *
         */
        RAT(Rat::build),

        /**
         *
         */
        BOMB(Bomb::build),

        /**
         *
         */
        DEATH_RAT(DeathRat::build),

        /**
         *
         */
        FEMALE_SEX_CHANGE(FemaleSexChange::build),

        /**
         *
         */
        GAS(Gas::build),

        /**
         *
         */
        MALE_SEX_CHANGE(MaleSexChange::build),

        /**
         *
         */
        NO_ENTRY(NoEntry::build),

        /**
         *
         */
        POISON(Poison::build),

        /**
         *
         */
        STERILISATION(Sterilisation::build);

        /**
         * Factory object used to construct this Entity from an Args string.
         */
        private final EntityFactory factory;

        /**
         * Constructs an entity from the base entity factory.
         *
         * @param factory Object that will produce the target entity.
         */
        ConstructableEntity(final EntityFactory factory) {
            this.factory = factory;
        }

        /**
         * @return Regex that will match this entity loader args.
         */
        @Override
        public Pattern getRegex() {
            final String base = "(?i)\\[(%s|%s),\\[(.*?)],\\[(.*?)]]";
            return Pattern.compile(String.format(
                    base,
                    name(),
                    name().replaceAll("_", "")
            ));
        }

        /**
         * Constructs a new instance of the target entity using the arguments
         * provided.
         *
         * @param args Arguments used to construct this entity.
         * @return Newly constructed entity.
         * @throws InvalidArgsContent If the args string cannot be safely
         *                            parsed into the target entity.
         */
        public Entity build(final String args)
                throws InvalidArgsContent, ImproperlyFormattedArgs {
            final Matcher m = getRegex().matcher(args);
            final int argsGroup = 2;
            final String delimiter = ",";
            if (m.find()) {
                return factory.create(m.group(argsGroup).split(delimiter));
            } else {
                throw new ImproperlyFormattedArgs(args);
            }
        }
    }

    /**
     * Hide constructor.
     */
    private EntityLoader() {
    }

    /**
     * @param args
     * @return
     */
    public static Entity build(final String args)
            throws InvalidArgsContent, ImproperlyFormattedArgs {
        Objects.requireNonNull(args);
        final String formatted = args.replaceAll("\\s", "");

        // Attempt to find match
        for (ConstructableEntity entity : ConstructableEntity.values()) {
            if (entity.getRegex().matcher(formatted).find()) {
                return entity.build(formatted);
            }
        }

        throw new IllegalStateException("Error");
    }

    //<----------------TEST CODE LOADS A GAS
    // OBJECT AND HAS IT OCCUPY TILES--------------------------->\\
    public static void main(String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {

        Rat r = new Rat(0, 0, 100, Rat.Sex.MALE,
                Rat.Age.ADULT, 5000, true, false
        );
        System.out.println(r.buildToString(null));

        Entity e = build(r.buildToString(null));
        System.out.println(e.buildToString(null));

        Gas gas = new Gas(0, 0, 50);
        Tile[][] tiles = new Tile[5][5];
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                tiles[row][col] = new Grass(GrassSprite.BARE_GRASS, row, col);
            }
        }

        ContextualMap map = new ContextualMap(tiles, 5, 5);

        map.placeIntoGame(gas);

        boolean isRight = true;
        TileData data = map.getOriginTile(gas);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (isRight) {
                    data = map.traverse(CardinalDirection.EAST, data);
                } else {
                    data = map.traverse(CardinalDirection.WEST, data);
                }
                map.occupyTile(gas, data);
            }
            data = map.traverse(CardinalDirection.SOUTH, data);
            isRight = !isRight;
        }

        Entity ent = build(gas.buildToString(map));
        System.out.println(gas.buildToString(map));

        // the map isn't setup for this newly constructed entity
        map.placeIntoGame(ent);
        System.out.println(ent.buildToString(map));
    }
}
