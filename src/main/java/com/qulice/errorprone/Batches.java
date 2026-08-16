/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Environment;
import com.qulice.spi.Relative;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

/**
 * Java sources split the way Maven compiles them: one batch per source
 * root, never two roots in one batch.
 *
 * <p>A single {@code javac} pass over two roots would manufacture
 * diagnostics no project could ever silence, because Maven lets every
 * source root hold a twin of the same file. The clearest case is
 * {@code package-info.java}, which Qulice's own {@code JavadocPackage}
 * check demands in every package: with one pass, a package present in two
 * roots earns {@code a package-info.java file has already been seen} and,
 * once the file carries an annotation, {@code has already been
 * annotated}.</p>
 *
 * <p>The roots a file may sit under are the ones the project declares,
 * which is what {@link Environment#testdirs()} reports. Matching the
 * {@code /src/test/} path fragment instead would miss every further root a
 * project registers through
 * {@code build-helper-maven-plugin:add-test-source} — {@code src/mock/java}
 * in the jcabi parent POM (see
 * <a href="https://github.com/yegor256/qulice/issues/1742">#1742</a>).
 * Folding all of those roots into one {@code test} batch does not help
 * either: {@code src/test/java} and {@code src/mock/java} are as free to
 * hold twins as {@code src/main/java} and {@code src/test/java} are, so
 * each root earns a batch of its own (see
 * <a href="https://github.com/yegor256/qulice/issues/1745">#1745</a>). A
 * file under none of them belongs to the main batch. Cross-batch
 * references still resolve, since the project's own compiled classes are
 * on the {@code -classpath} of every pass.</p>
 *
 * @since 1.0
 */
public final class Batches {

    /**
     * Name of the batch of the sources that sit under no declared test
     * source root. Every other batch this class makes holds test sources,
     * which is how {@link Release} tells the two apart.
     */
    static final String MAIN = "main";

    /**
     * Runs of characters that have no place in a file name, in the path of
     * a source root that {@link #label(File, int)} turns into a batch name.
     */
    private static final Pattern SEPARATOR = Pattern.compile("[^A-Za-z0-9]+");

    /**
     * The dashes {@link #SEPARATOR} leaves at either end of a batch name.
     */
    private static final Pattern EDGES = Pattern.compile("^-+|-+$");

    /**
     * Environment that knows the source roots.
     */
    private final Environment env;

    /**
     * Java source files to split.
     */
    private final List<File> sources;

    /**
     * Constructor.
     * @param env Environment that knows the source roots
     * @param sources Java source files to split
     */
    public Batches(final Environment env, final List<File> sources) {
        this.env = env;
        this.sources = sources;
    }

    /**
     * Split the sources, one batch per source root.
     * @return Batches by name, in compilation order, none of them empty
     */
    public Map<String, List<File>> split() {
        final Map<String, String> roots = new LinkedHashMap<>(0);
        int index = 0;
        for (final File dir : this.env.testdirs()) {
            index += 1;
            roots.putIfAbsent(
                Batches.slashed(dir).concat("/"), this.label(dir, index)
            );
        }
        final Map<String, List<File>> batches = new LinkedHashMap<>(0);
        batches.put(Batches.MAIN, new ArrayList<>(0));
        for (final String name : roots.values()) {
            batches.put(name, new ArrayList<>(0));
        }
        for (final File source : this.sources) {
            batches.get(
                Batches.owner(Batches.slashed(source), roots)
            ).add(source);
        }
        batches.values().removeIf(List::isEmpty);
        return batches;
    }

    /**
     * The name of the batch of the given source root.
     *
     * <p>Batch names end up inside file names, so the path of the root is
     * reduced to letters and digits. The position of the root among the
     * ones the project declares is prepended, because that reduction is not
     * injective — {@code src/mock-java} and {@code src/mock/java} share a
     * shape — and two roots sharing a name would land in one batch, which
     * is the very thing this split prevents.</p>
     *
     * @param root The source root
     * @param index Its position among the roots, one-based
     * @return Name of the batch, safe to use in a file name
     */
    private String label(final File root, final int index) {
        return String.format(
            "test-%d-%s",
            index,
            Batches.EDGES.matcher(
                Batches.SEPARATOR.matcher(
                    new Relative(this.env.basedir(), root).path()
                ).replaceAll("-")
            ).replaceAll("")
        );
    }

    /**
     * The batch a source file belongs to: the root it sits under, or the
     * main batch when it sits under none of them.
     *
     * <p>The longest matching root wins, so that a root nested inside
     * another one — {@code src/test} and {@code src/test/java} both
     * declared, say — claims its own files instead of leaving them to the
     * root above.</p>
     *
     * @param path Normalized absolute path of the file
     * @param roots Batch name by root path, each ending with a slash
     * @return Name of the batch
     */
    private static String owner(final String path,
        final Map<String, String> roots) {
        String name = Batches.MAIN;
        int longest = 0;
        for (final Map.Entry<String, String> root : roots.entrySet()) {
            final String dir = root.getKey();
            if (dir.length() > longest && path.startsWith(dir)) {
                longest = dir.length();
                name = root.getValue();
            }
        }
        return name;
    }

    /**
     * The absolute path of a file, in one shape: forward slashes, no
     * {@code .} or {@code ..} steps. Source roots arrive from the POM
     * while sources arrive from walking the disk, so only a normalized
     * form makes the two comparable as text.
     * @param file File to render
     * @return Its absolute path
     */
    private static String slashed(final File file) {
        final String absolute = file.getAbsolutePath();
        String path = FilenameUtils.normalize(absolute, true);
        if (path == null) {
            path = absolute.replace(File.separatorChar, '/');
        }
        return path;
    }
}
