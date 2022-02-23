package game.level.writer;

import game.level.levels.template.TemplateElement;
import game.level.reader.RatGameFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;

/**
 * Class designed to be able to write modules to a default rat game file; more
 * specifically from a given {@link game.level.reader.RatGameFile} this will
 * write to the literal file any module that is requested to be written to.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class RatGameFileWriter {

    /**
     * The format of the module (section in a rat game file).
     */
    public enum ModuleFormat {

        /**
         * The format of the Leaderboard module. Includes the player name,
         * score and time left.
         */
        LEADERBOARD("LEADERBOARD {%n%s%n}%n",
                RatGameFile.Module.LEADERBOARD.getRegex().toString()
        );

        /**
         * The format string to use when serialising the leaderboard to a
         * string.
         */
        private final String template;

        /**
         * The ReGeX used when replacing the parameters of a given player in
         * the leaderboard module.
         */
        private final String replaceRegex;

        /**
         * Constructor for the generalised module format.
         *
         * @param formatTemplate The format string used when serialising.
         * @param matchRegex     The ReGeX used when replacing parameters.
         */
        ModuleFormat(final String formatTemplate,
                     final String matchRegex) {
            this.template = formatTemplate;
            this.replaceRegex = matchRegex;
        }

        /**
         * Gets the template used to serialise the parameters.
         *
         * @return The template used to serialise the parameters.
         */
        public String getTemplate() {
            return template;
        }
    }

    /**
     * The base file used when writing, this file contains the content that
     * will be modified, and will be written back to when committed.
     */
    private final RatGameFile baseFile;

    /**
     * The modified content that will be used to modify the base file.
     */
    private String modifiedContent;

    /**
     * Creates a new file writer with a reference to a base file.
     *
     * @param file The base file to write modified content to.
     */
    public RatGameFileWriter(final RatGameFile file) {
        this.baseFile = file;
        this.modifiedContent = file.getContent();
    }

    /**
     * Write the given content to a given module of the file. This will
     * replace the module's content with the given string.
     *
     * @param module  The module to replace the content with.
     * @param content The content to use when replacing the module.
     */
    public void writeModule(final ModuleFormat module,
                            final String content) {
        modifiedContent = modifiedContent.replaceAll(
                module.replaceRegex,
                Matcher.quoteReplacement(
                        String.format(module.template, content))
        );
    }

    /**
     * Writes to the file the first instance of the template elem the
     * provided string.
     *
     * @param elem    The element to update.
     * @param content The content to update it with.
     */
    public void writeElement(final TemplateElement elem,
                             final String content) {
        modifiedContent = modifiedContent.replaceFirst(
                elem.getRegex().pattern(),
                Matcher.quoteReplacement(
                        elem.getPadChar()
                                + content
                                + elem.getPadChar())
        );
    }

    /**
     * @return Mutated string content.
     */
    public String getModifiedContent() {
        return modifiedContent;
    }

    /**
     * Write the modified content of the file to the base file's location,
     * overwriting the file (or creating a new file should it not exist).
     *
     * @throws IOException Exception due to read/write access.
     */
    public void commitToFile() throws IOException {
        Files.writeString(
                Path.of(baseFile.getDefaultFile()),
                this.modifiedContent,
                StandardCharsets.UTF_8
        );
    }
}
