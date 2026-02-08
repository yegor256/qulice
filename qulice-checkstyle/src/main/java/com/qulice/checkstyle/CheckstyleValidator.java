/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.jcabi.log.Logger;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.qulice.spi.Environment;
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.xml.sax.InputSource;

/**
 * Validator with Checkstyle.
 *
 * @since 0.3
 * @checkstyle ClassDataAbstractionCoupling (260 lines)
 */
public final class CheckstyleValidator implements ResourceValidator {

    /**
     * Checkstyle checker.
     */
    private final Checker checker;

    /**
     * Listener of checkstyle messages.
      */
    private final CheckstyleListener listener;

    /**
     * Environment to use.
     */
    private final Environment env;

    /**
     * Constructor.
     * @param env Environment to use.
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    public CheckstyleValidator(final Environment env) {
        this.env = env;
        this.checker = new Checker();
        this.checker.setModuleClassLoader(
            Thread.currentThread().getContextClassLoader()
        );
        try {
            this.checker.configure(this.configuration());
        } catch (final CheckstyleException ex) {
            throw new IllegalStateException("Failed to configure checker", ex);
        }
        this.listener = new CheckstyleListener(this.env);
        this.checker.addListener(this.listener);
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Collection<Violation> validate(final Collection<File> files) {
        final List<File> sources = this.getNonExcludedFiles(files);
        try {
            Logger.debug(this, "Checkstyle processing %d files", sources.size());
            final long start = System.currentTimeMillis();
            this.checker.process(sources);
            Logger.debug(
                this,
                "Checkstyle processed %d files in %[ms]s",
                sources.size(),
                System.currentTimeMillis() - start
            );
        } catch (final CheckstyleException ex) {
            throw new IllegalStateException("Failed to process files", ex);
        }
        final List<AuditEvent> events = this.listener.events();
        final Collection<Violation> results = new LinkedList<>();
        for (final AuditEvent event : events) {
            final String check = event.getSourceName();
            results.add(
                new Violation.Default(
                    this.name(),
                    check.substring(check.lastIndexOf('.') + 1),
                    event.getFileName(),
                    String.valueOf(event.getLine()),
                    event.getMessage()
                )
            );
        }
        return results;
    }

    @Override public String name() {
        return "Checkstyle";
    }

    /**
     * Filters out excluded files from further validation.
     * @param files Files to validate
     * @return List of relevant files
     */
    public List<File> getNonExcludedFiles(final Collection<File> files) {
        final List<File> relevant = new LinkedList<>();
        for (final File file : files) {
            final String name = file.getPath().substring(
                this.env.basedir().toString().length()
            );
            if (this.env.exclude("checkstyle", name)) {
                continue;
            }
            if (!name.toLowerCase(java.util.Locale.ROOT).endsWith(".java")) {
                continue;
            }
            relevant.add(file);
        }
        return relevant;
    }

    /**
     * Load checkstyle configuration.
     * @return The configuration just loaded
     * @see #validate(Collection)
     */
    private Configuration configuration() {
        final File cache =
            new File(this.env.tempdir(), "checkstyle/checkstyle.cache");
        final File parent = cache.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException(
                String.format(
                    "Unable to create directories needed for %s at %s",
                    cache.getPath(), parent.getAbsolutePath()
                )
            );
        }
        if (!parent.canWrite()) {
            throw new IllegalStateException(
                String.format(
                    "Unable to write to directory %s, check permissions.",
                    parent.getAbsolutePath()
                )
            );
        }
        final Properties props = new Properties();
        props.setProperty("cache.file", cache.getPath());
        final Configuration config;
        try (java.io.InputStream stream = this.getClass().getResourceAsStream("checks.xml")) {
            if (stream == null) {
                throw new IllegalStateException(
                    "Checkstyle configuration file 'checks.xml' not found in classpath."
                );
            }
            final InputSource src = new InputSource(stream);
            config = ConfigurationLoader.loadConfiguration(
                src,
                new PropertiesExpander(props),
                ConfigurationLoader.IgnoredModulesOptions.OMIT
            );
        } catch (final CheckstyleException | java.io.IOException ex) {
            throw new IllegalStateException("Failed to load config", ex);
        }
        return config;
    }
}
