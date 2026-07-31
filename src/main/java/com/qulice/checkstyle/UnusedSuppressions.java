/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.qulice.spi.Environment;
import com.qulice.spi.Relative;
import com.qulice.spi.Violation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import org.xml.sax.InputSource;

/**
 * The {@code @checkstyle} suppressions that cover no violation.
 *
 * <p>Checkstyle honors a comment like {@code @checkstyle TypeName (N
 * lines)} through its suppression filters but reports nothing when the
 * suppressed lines hold no violation of {@code TypeName}, so the comment
 * lingers as a lie about the code. {@link UnknownSuppressionCheck} already
 * rejects a name that no enabled check answers to; this class covers the
 * other half, the way PMD does with {@code UnnecessaryWarningSuppression},
 * by running Checkstyle a second time with the nearby filters removed and
 * reporting each suppression whose range holds no event of its check.</p>
 *
 * <p>The second run leaves alone the files that the first run took from
 * its cache, since Checkstyle collects no events for them, and their
 * suppressions cannot be judged.</p>
 *
 * @since 1.0
 */
final class UnusedSuppressions {

    /**
     * Filters of {@code checks.xml} that honor the {@code @checkstyle}
     * comments. Dropping them lets the second run see every violation the
     * comments mean to hide.
     */
    private static final Set<String> FILTERS = Set.of(
        "SuppressWithNearbyCommentFilter",
        "SuppressWithNearbyTextFilter",
        "SuppressWithPlainTextCommentFilter"
    );

    /**
     * Environment to use.
     */
    private final Environment env;

    /**
     * Constructor.
     * @param env Environment to use
     */
    UnusedSuppressions(final Environment env) {
        this.env = env;
    }

    /**
     * Find the suppressions that cover no violation.
     * @param files Files the primary run processed, cache aside
     * @return Violations, one per dead suppression
     */
    Collection<Violation> validate(final Collection<File> files) {
        final Collection<Violation> results = new ArrayList<>(0);
        if (!files.isEmpty()) {
            final CheckstyleListener sink = new CheckstyleListener(this.env);
            this.collect(files, sink);
            for (final File file : files) {
                results.addAll(this.unused(file, sink.events()));
            }
        }
        return results;
    }

    /**
     * Run Checkstyle over the files with the nearby filters removed.
     * @param files Files to process
     * @param sink Listener that gathers the events
     */
    private void collect(final Collection<File> files,
        final CheckstyleListener sink) {
        final Checker checker = new Checker();
        checker.setModuleClassLoader(
            Thread.currentThread().getContextClassLoader()
        );
        final File cache = this.cache();
        try {
            checker.configure(this.configuration(cache));
            checker.addListener(sink);
            checker.process(new ArrayList<>(files));
        } catch (final CheckstyleException ex) {
            throw new IllegalStateException(
                "Failed to re-run Checkstyle without suppression filters", ex
            );
        } finally {
            checker.destroy();
            cache.delete();
        }
    }

    /**
     * The dead suppressions of one file.
     * @param file File to inspect
     * @param events Events collected with the nearby filters removed
     * @return Violations, one per dead suppression
     */
    private Collection<Violation> unused(final File file,
        final Collection<AuditEvent> events) {
        final Collection<Violation> results = new ArrayList<>(0);
        if (!this.env.exclude(
            "checkstyle", new Relative(this.env.basedir(), file).path()
        )) {
            final Collection<AuditEvent> here = new ArrayList<>(0);
            for (final AuditEvent event : events) {
                if (file.getPath().equals(event.getFileName())) {
                    here.add(event);
                }
            }
            for (final SuppressionTag tag : new Suppressions(this.read(file))) {
                if (tag.unused(here)) {
                    results.add(
                        new Violation.Default(
                            "Checkstyle",
                            "UnusedSuppressionCheck",
                            file.getPath(),
                            String.valueOf(tag.line()),
                            String.format(
                                "This suppression covers no violation of \"%s\"",
                                tag.check()
                            )
                        )
                    );
                }
            }
        }
        return results;
    }

    /**
     * Read the text of a file, in the encoding of the environment.
     * @param file File to read
     * @return Its text
     */
    private String read(final File file) {
        try {
            return Files.readString(file.toPath(), this.env.encoding());
        } catch (final IOException ex) {
            throw new IllegalStateException(
                String.format("Failed to read %s", file), ex
            );
        }
    }

    /**
     * A fresh cache file, so the second run reprocesses every file.
     * @return Path to an empty cache file
     */
    private File cache() {
        final File dir = new File(this.env.tempdir(), "checkstyle");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException(
                String.format("Unable to create %s", dir)
            );
        }
        try {
            return Files.createTempFile(dir.toPath(), "unused", ".cache").toFile();
        } catch (final IOException ex) {
            throw new IllegalStateException(
                "Failed to create a cache file for the second Checkstyle run", ex
            );
        }
    }

    /**
     * The configuration of {@code checks.xml} without the nearby filters.
     * @param cache Cache file for the run
     * @return The configuration
     */
    private Configuration configuration(final File cache) {
        final Properties props = new Properties();
        props.setProperty("cache.file", cache.getPath());
        final Configuration config;
        try (InputStream stream = this.getClass().getResourceAsStream("checks.xml")) {
            if (stream == null) {
                throw new IllegalStateException(
                    "Checkstyle configuration file 'checks.xml' not found in classpath."
                );
            }
            config = ConfigurationLoader.loadConfiguration(
                new InputSource(stream),
                new PropertiesExpander(props),
                ConfigurationLoader.IgnoredModulesOptions.OMIT
            );
        } catch (final CheckstyleException | IOException ex) {
            throw new IllegalStateException("Failed to load config", ex);
        }
        UnusedSuppressions.strip(config);
        return config;
    }

    /**
     * Remove the nearby suppression filters from a configuration tree.
     * @param config Configuration to strip, modified in place
     */
    private static void strip(final Configuration config) {
        for (final Configuration child : config.getChildren()) {
            final String name = child.getName();
            final String simple = name.substring(name.lastIndexOf('.') + 1);
            if (UnusedSuppressions.FILTERS.contains(simple)) {
                ((DefaultConfiguration) config).removeChild(child);
            } else {
                UnusedSuppressions.strip(child);
            }
        }
    }
}
