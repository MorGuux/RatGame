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
 *
 */
public final class EntityLoader {

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

    public static void main(String[] args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {
        final String arg = "[Rat,[1,1,50,MALE,ADULT,TRUE,TRUE,-1],[(0,1)]]";
        final String deathRatArg = "[DeathRat, [0, 1, 100, 5], []]";

        Entity rat = build(arg);
        System.out.println(rat.buildToString(null));

        Entity deathRat = build(deathRatArg);
        System.out.println(deathRat.buildToString(null));
    }
}
