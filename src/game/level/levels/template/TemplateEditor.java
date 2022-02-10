package game.level.levels.template;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.stream.Stream;

/**
 * Java class created on 10/02/2022 for usage in project RatGame-A2. Class
 * wraps a basic empty .RGF file which can have any element written to it at
 * any time.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class TemplateEditor {

    /**
     * Path pointing to te default template file we write to.
     */
    private static final Path TEMPLATE_FILE
            = Path.of("src/game/level/levels/template/TemplateDefaultFile.rgf");

    /**
     * Custom level's directory path from src root.
     */
    public static final String CUSTOM_FILES_DIR
            = "src/game/level/levels/template/custom";

    /**
     * Mutable string which is constantly being updated through the
     * {@link #setElement(TemplateElement, String)} method.
     */
    private String modifiedContent;

    /**
     * Constructs a template editor and initialises the base dependencies.
     *
     * @throws IOException If the dependencies could not load.
     */
    public TemplateEditor() throws IOException {
        // Load the template file into memory
        final StringJoiner sj = new StringJoiner(System.lineSeparator());
        try (Stream<String> s = Files.lines(TEMPLATE_FILE)) {
            s.forEach(sj::add);
        }
        this.modifiedContent = sj.toString();
    }

    /**
     * Sets the value of the provided element to the provided string content.
     *
     * @param elem    The element to write/update.
     * @param content The value of this element.
     * @implNote This does no sanity checks on the data it is entirely the
     * callers' responsibility to do this.
     */
    public void setElement(final TemplateElement elem,
                           final String content) {
        this.modifiedContent = modifiedContent.replaceFirst(
                elem.getRegex().pattern(),
                Matcher.quoteReplacement(elem.getPadChar()
                        + content
                        + elem.getPadChar())
        );
    }

    /**
     * @return Raw string content that may have values written and modified.
     */
    public String getModifiedContent() {
        return this.modifiedContent;
    }

    /**
     * Writes the string content held in this template editor to the target
     * file.
     *
     * @param f The file to write to.
     * @throws IOException If one occurs whilst attempting to write to the file.
     */
    public void writeContentToFile(final File f) throws IOException {
        // I am used to Files.write(String.getBytes(UTF-8)); this method is
        // equivalent according to the JavaDoc
        Files.writeString(
                f.toPath(),
                this.getModifiedContent()
        );
    }
}
