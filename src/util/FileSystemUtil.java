package util;

import game.level.levels.template.TemplateEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Java class created on 12/02/2022 for usage in project RatGame-A2. Class
 * consists primarily of File System operations that are often used in the
 * program.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public final class FileSystemUtil {

    /**
     * String path from source root to the default levels directory.
     */
    public static final String DEFAULT_LEVELS_DIR = "src/game/level/levels";

    /**
     * Hide this.
     */
    private FileSystemUtil() {
        // Hide constructor
    }

    /**
     * @param f The file to check.
     * @return {@code true} if the given file is a Rat Game Default File.
     */
    public static boolean isRatGameFile(final File f) {
        return (f != null)
                && f.isFile()
                && f.getName().matches("(?i).*?\\.rgf");
    }

    /**
     * @param f The file to check.
     * @return {@code true} if the given file is a Rat Game Save File.
     */
    public static boolean isRatGameSaveFile(final File f) {
        return (f != null)
                && f.isFile()
                && f.getName().matches("(?i).*?\\.rgs");
    }

    /**
     * @return All custom level .RGF files.
     */
    public static File[] getAllCustomLevels() {
        return collectAllTrue(
                new File(TemplateEditor.CUSTOM_FILES_DIR),
                FileSystemUtil::isRatGameFile
        );
    }

    /**
     * @return All base game .RGF files.
     */
    public static File[] getAllBaseGameLevels() {
        return collectAllTrue(
                new File(DEFAULT_LEVELS_DIR),
                FileSystemUtil::isRatGameFile
        );
    }

    /**
     * Reads the provided directory collecting all files which match the given
     * predicate.
     *
     * @param dir The directory to read through. Only the top level is checked.
     * @param fn The predicate to apply to all files collecting it if true.
     * @return All files found that resulted true to the given predicate.
     */
    private static File[] collectAllTrue(final File dir,
                                         final Predicate<File> fn) {
        final File[] files = dir.listFiles();

        final List<File> rgfFiles = new LinkedList<>();
        if (files != null) {
            Arrays.stream(files)
                    .filter(fn)
                    .forEach(rgfFiles::add);
        }

        return rgfFiles.toArray(new File[0]);
    }

    /**
     * Sorts the results from {@link #getAllCustomLevels()} and
     * {@link #getAllBaseGameLevels()} into a single array which is ordered
     * based around when it was last modified in ascending order.
     *
     * @return Sorted array in ascending order. (Latest modified last).
     */
    public static File[] getLatestReadRgfFiles() {
        final Comparator<File> func
                = Comparator.comparingLong(File::lastModified);
        final List<File> files = new ArrayList<>();

        Collections.addAll(files, getAllBaseGameLevels());
        Collections.addAll(files, getAllCustomLevels());

        files.sort(func);

        return files.toArray(new File[0]);
    }
}
