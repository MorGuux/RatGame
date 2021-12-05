package game.level.writer;

import game.level.reader.RatGameFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
     *
     */
    public enum ModuleFormat {

        /**
         *
         */
        LEADERBOARD("LEADERBOARD {%n%s%n}%n",
                RatGameFile.Module.LEADERBOARD.getRegex().toString()
        );

        /**
         *
         */
        private final String template;

        /**
         *
         */
        private final String replaceRegex;

        /**
         *
         * @param template
         * @param matchRegex
         */
        ModuleFormat(final String template,
                     final String matchRegex) {
            this.template = template;
            this.replaceRegex = matchRegex;
        }

        /**
         *
         * @return
         */
        public String getTemplate() {
            return template;
        }

        /**
         *
         * @return
         */
        public String getReplaceRegex() {
            return replaceRegex;
        }
    }

    /**
     *
     */
    private final RatGameFile baseFile;

    /**
     *
     */
    private String modifiedContent;

    /**
     *
     * @param file
     */
    public RatGameFileWriter(final RatGameFile file) {
        this.baseFile = file;
        this.modifiedContent = file.getContent();
    }

    /**
     *
     * @param module
     * @param content
     */
    public void writeModule(final ModuleFormat module,
                            final String content) {
        modifiedContent = modifiedContent.replaceAll(
                module.replaceRegex,
                String.format(module.template, content)
        );
    }

    /**
     *
     * @throws IOException
     */
    public void commitToFile() throws IOException {
            Files.writeString(
                    Path.of(baseFile.getDefaultFile()),
                    this.modifiedContent,
                    StandardCharsets.UTF_8
            );
    }

    /**
     *
     * @return
     */
    public RatGameFile getBaseFile() {
        return baseFile;
    }
}
