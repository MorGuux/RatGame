package game.entity.loader;

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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static loader class designed to filter a given args string into a proper
 * Entity factory that can then produce the target entity of the args.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
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
     * Attempts to deduce from the provided args what Entity it is and then
     * attempts to use the data to construct that entity.
     *
     * @param args Args string that could/should be compilable into an Entity.
     * @return The entity that the args was identified to be.
     * @throws ImproperlyFormattedArgs if the String can not be parsed.
     * @throws InvalidArgsContent if the arguments are not formatted correctly.
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

    /**
     * Hide constructor.
     */
    private EntityLoader() {
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
    public enum ConstructableEntity implements RatGameFile.RegexModule {

        /**
         * Wraps the construction of a normal Rat.
         */
        RAT(Rat::build, Rat.class),

        /**
         * Wraps the construction of a normal bomb.
         */
        BOMB(Bomb::build, Bomb.class),

        /**
         * Wraps the construction of a normal death rat.
         */
        DEATH_RAT(DeathRat::build, DeathRat.class),

        /**
         * Wraps the construction of a normal female sex change.
         */
        FEMALE_SEX_CHANGE(FemaleSexChange::build, FemaleSexChange.class),

        /**
         * Wraps the construction of a normal gas.
         */
        GAS(Gas::build, Gas.class),

        /**
         * Wraps the construction of a normal male sex change.
         */
        MALE_SEX_CHANGE(MaleSexChange::build, MaleSexChange.class),

        /**
         * Wraps the construction of a normal no entry.
         */
        NO_ENTRY(NoEntry::build, NoEntry.class),

        /**
         * Wraps the construction of a normal poison.
         */
        POISON(Poison::build, Poison.class),

        /**
         * Wraps the construction of a normal sterilisation.
         */
        STERILISATION(Sterilisation::build, Sterilisation.class);

        /**
         * Factory object used to construct this Entity from an Args string.
         */
        private final EntityFactory factory;

        /**
         * Target class of the factory object.
         */
        private final Class<? extends Entity> target;

        /**
         * Constructs an entity from the base entity factory.
         *
         * @param entityFactory Object that will produce the target entity.
         */
        ConstructableEntity(final EntityFactory entityFactory,
                            final Class<? extends Entity> target) {
            this.factory = entityFactory;
            this.target = target;
        }

        /**
         * @return Entity factory used to create new entity objects.
         */
        public EntityFactory getFactory() {
            return factory;
        }

        /**
         * @return Target class of the entity object.
         */
        public Class<? extends Entity> getTarget() {
            return target;
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
         * @throws ImproperlyFormattedArgs if the String can not be parsed.
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
}
