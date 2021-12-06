package game.generator.loader;

import game.entity.Item;
import game.entity.subclass.bomb.Bomb;
import game.entity.subclass.deathRat.DeathRat;
import game.entity.subclass.femaleSexChange.FemaleSexChange;
import game.entity.subclass.gas.Gas;
import game.entity.subclass.maleSexChange.MaleSexChange;
import game.entity.subclass.noentry.NoEntry;
import game.entity.subclass.poison.Poison;
import game.entity.subclass.sterilisation.Sterilisation;
import game.generator.ItemFactory;
import game.generator.ItemGenerator;
import game.level.reader.RatGameFile;
import game.level.reader.exception.ImproperlyFormattedArgs;
import game.level.reader.exception.InvalidArgsContent;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static loader class that will load up an Item Generator using the provided
 * args string.
 * <p>
 * Note: Class looks complicated, but it's only because we're working
 * around Type args. Note that adding a new item to the game is as simple
 * as just adding a new entry to the GeneratorTarget.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public final class ItemGeneratorLoader {

    /**
     * Matches strings that are in the format but does not ensure that the
     * data held within is actually proper.
     */
    public static final Pattern SOFT_MATCH_REGEX
            = Pattern.compile("\\[([a-zA-Z]+|[a-zA-Z_]+),\\[[0-9]+,[0-9]+,"
            + "[0-9]+,[0-9]+]]");

    /**
     * Error message for when the formatting of string passed into a method
     * cannot be parsed into the expected data due to the formatting being
     * unknown.
     */
    private static final String ERR_FORMATTING_BAD = "Cannot parse the args: "
            + "\"%s\" as the formatting does not meet the expected: \"%s\"...";

    /**
     * Error message for when the target class could not be deduced from the
     * provided args.
     */
    private static final String ERR_UNKNOWN_TARGET = "The args: \"%s\" could "
            + "not be parsed as the underlying Target class could not be "
            + "deduced.%nExpected targets are: %s...";

    /**
     * Error message for when an argument cannot be parsed into data which
     * can be used.
     */
    private static final String ERR_ARG_BAD = "The arg: \"%s\" in the args: "
            + "\"%s\" could not be parsed%nbecause it does not meet the "
            + "expected: %s...";

    //todo factory template should exist in its own file or be an inner
    // interface to ItemGenerator.

    /**
     * Factory template for constructing a Generator.
     *
     * @param <T> The target type for the generator.
     * @author -Ry
     * @version 0.1
     * Copyright: N/A
     */
    private interface ItemGeneratorFactory<T extends Item> {

        /**
         * Construct this ItemGenerator from the provided base arguments.
         *
         * @param refreshTime        Time in milliseconds to wait inorder to be
         *                           given more usages.
         * @param currentRefreshTime Current time in the refresh state.
         * @param currentUsages      Current number of usages available for the
         *                           generator.
         * @param maxUsages          Maximum ever allowed number of usages.
         * @return Newly constructed item generator.
         */
        ItemGenerator<T> create(int refreshTime,
                                int currentRefreshTime,
                                int currentUsages,
                                int maxUsages);
    }

    /**
     * Enumerates all the known Items in the game allowing explicit type
     * construction of the generator at the base target item. Designed to
     * work around {@link ItemGenerator#buildToString()} so that an item can
     * be loaded directly from a String.
     *
     * @author -Ry
     * @version 0.1
     * Copyright: N/A
     */
    private enum GeneratorTarget implements RatGameFile.RegexModule {
        /**
         * Represents Death Rat target generators.
         */
        DEATH_RAT(createFactory(
                DeathRat.class, DeathRat::new
        )),

        /**
         * Represents Gas target generators.
         */
        GAS(createFactory(
                Gas.class, Gas::new
        )),

        /**
         * Represents bomb target generators.
         */
        BOMB(createFactory(
                Bomb.class, Bomb::new
        )),

        /**
         * Represents female sex change target generators.
         */
        FEMALE_SEX_CHANGE(createFactory(
                FemaleSexChange.class, FemaleSexChange::new
        )),

        /**
         * Represents male sex change target generators.
         */
        MALE_SEX_CHANGE(createFactory(
                MaleSexChange.class, MaleSexChange::new
        )),

        /**
         * Represents no entry target generators.
         */
        NO_ENTRY(createFactory(
                NoEntry.class, NoEntry::new
        )),

        /**
         * Represents poison target generators.
         */
        POISON(createFactory(
                Poison.class, Poison::new
        )),

        /**
         * Represents sterilisation target generators.
         */
        STERILISATION(createFactory(
                Sterilisation.class, Sterilisation::new
        ));


        /**
         * Convenience method for supplying a generator factory of the target
         * class.
         *
         * @param target  The target for generator factory.
         * @param factory The construction method to instantiate the target
         *                class.
         * @param <T>     The type of the target class to generate.
         * @return Newly constructed generator factory.
         */
        private static <T extends Item> ItemGeneratorFactory<T> createFactory(
                final Class<T> target,
                final ItemFactory<T> factory) {
            // Without this, we would need to do this for all entries
            return (refreshTime, currentRefreshTime,
                    currentUsages, maxUsages) ->
                    new ItemGenerator<>(
                            target,
                            factory,
                            refreshTime,
                            currentRefreshTime,
                            currentUsages,
                            maxUsages
                    );
        }

        /**
         * The base generator factory.
         */
        private final ItemGeneratorFactory<?> factory;

        <T extends Item> GeneratorTarget(final ItemGeneratorFactory<T> fact) {
            this.factory = fact;
        }

        /**
         * Construct this ItemGenerator from the provided base arguments.
         *
         * @param refreshTime        Time in milliseconds to wait inorder to be
         *                           given more usages.
         * @param currentRefreshTime Current time in the refresh state.
         * @param currentUsages      Current number of usages available for the
         *                           generator.
         * @param maxUsages          Maximum ever allowed number of usages.
         * @return Newly constructed item generator.
         */
        public ItemGenerator<?> create(final int refreshTime,
                                       final int currentRefreshTime,
                                       final int currentUsages,
                                       final int maxUsages) {
            return factory.create(refreshTime,
                    currentRefreshTime,
                    currentUsages,
                    maxUsages
            );
        }

        /**
         * Gets a regex for this module. The regex will match:
         * <ul>
         *     <li>[A,[1,2,3,4]]</li>
         *     <li>[A_B,[1,2,3,4]]</li>
         * </ul>
         * Note the spaces; or the lack of.
         *
         * @return Regex that will match this module.
         */
        @Override
        public Pattern getRegex() {
            // Matches: "[A,[1,2,3,4]]" as is
            final String base = "(?i)\\[(%s|%s),\\[([0-9]+),([0-9]+),([0-9]+),"
                    + "([0-9]+)]";
            return Pattern.compile(String.format(
                    base,
                    name(),
                    name().replaceAll("_", "")
            ));
        }
    }

    /**
     * Hide constructor.
     */
    private ItemGeneratorLoader() {
    }

    /**
     * Build from the String args an ItemGenerator.
     *
     * @param args The arguments to use whilst building.
     * @return Newly constructed Item Generator.
     * @throws ImproperlyFormattedArgs if the String can not be parsed.
     * @throws InvalidArgsContent if the arguments are not formatted correctly.
     */
    public static ItemGenerator<? extends Item> build(final String args)
            throws ImproperlyFormattedArgs, InvalidArgsContent {

        // Formatting bad
        if (!isSoftMatch(args)) {
            throw new ImproperlyFormattedArgs(String.format(
                    ERR_FORMATTING_BAD,
                    args,
                    "[FOO,[INT,INT,INT,INT]]"
            ));
        }

        for (GeneratorTarget target : GeneratorTarget.values()) {
            final Matcher m = target.getRegex().matcher(args);
            if (m.find()) {
                final int refreshTimeGroup = 2;
                final int curRefreshTimeGroup = 3;
                final int curUsagesGroup = 4;
                final int maxUsagesGroup = 5;
                // parse int throws targeted errors
                return target.create(
                        parseAsInt(m.group(refreshTimeGroup), args),
                        parseAsInt(m.group(curRefreshTimeGroup), args),
                        parseAsInt(m.group(curUsagesGroup), args),
                        parseAsInt(m.group(maxUsagesGroup), args)
                );
            }
        }

        // Generator target unknown
        throw new InvalidArgsContent(String.format(
                ERR_UNKNOWN_TARGET,
                args,
                Arrays.deepToString(GeneratorTarget.values())
        ));
    }

    /**
     * Attempts to parse the provided arg into an integer.
     *
     * @param arg  The arg to parse.
     * @param args The base args it is from.
     * @return Parsed integer.
     * @throws InvalidArgsContent If the arg could not be parsed into an
     *                            integer.
     */
    private static int parseAsInt(final String arg,
                                  final String args) throws InvalidArgsContent {
        final String bounds = String.format(
                "(%s >= %s, %s <= %s)",
                arg,
                Integer.MIN_VALUE,
                arg,
                Integer.MAX_VALUE
        );
        // Try to parse value
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new InvalidArgsContent(String.format(
                    ERR_ARG_BAD,
                    arg,
                    args,
                    bounds
            ));
        }
    }

    /**
     * Checks to see if the provided args are properly setup. Utilising the
     * expected format. However, does not ensure that the data held within is
     * actually proper.
     *
     * @param args The build args to test.
     * @return {@code true} if the provided args are a soft match for the
     * expected. Otherwise, if not then {@code false} is returned.
     */
    public static boolean isSoftMatch(final String args) {
        return args.matches(SOFT_MATCH_REGEX.toString());
    }
}
