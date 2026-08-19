/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.jcabi.log.Logger;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.qulice.spi.Environment;
import com.qulice.spi.Relative;
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import org.xml.sax.InputSource;

/**
 * Validator with Checkstyle.
 * @since 0.3
 */
public final class CheckstyleValidator implements ResourceValidator {

    /**
     * Extensions of files that are passed to Checkstyle. These match the
     * file extensions referenced by checks in {@code checks.xml}. Checkstyle
     * itself filters further based on each module's {@code fileExtensions}.
     */
    private static final Set<String> EXTENSIONS = Set.of(
        "java", "txt", "xml", "xsl", "xsd", "properties", "groovy", "vm",
        "mf", "sh", "sql", "tokens", "g", "spec", "css", "csv", "js", "json",
        "md", "yml", "yaml", "gradle", "dtd", "scss", "html"
    );

    /**
     * Checks that suggest fixes relying on Java language features not
     * available under {@code -source 8}/{@code -target 8} (and other
     * pre-14 levels). Such a check must be stripped from the configuration
     * when the project targets an older Java, otherwise it recommends code
     * that fails to compile. {@code UseEnhancedSwitch} suggests arrow-switch
     * syntax, which is a Java 14+ feature (see #1694).
     */
    private static final Set<String> MODERN = Set.of("UseEnhancedSwitch");

    /**
     * The minimum Java source level at which the {@link #MODERN} checks are
     * applicable. Below this, they are removed from the configuration.
     */
    private static final int MINIMUM = 14;

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
     * @param env Environment to use
     */
    public CheckstyleValidator(final Environment env) {
        this.env = env;
        this.checker = new Checker();
        this.listener = new CheckstyleListener(this.env);
    }

    @Override
    public Collection<Violation> validate(final Collection<File> files) {
        this.checker.setModuleClassLoader(
            Thread.currentThread().getContextClassLoader()
        );
        try {
            this.checker.configure(this.configuration());
        } catch (final CheckstyleException ex) {
            throw new IllegalStateException("Failed to configure checker", ex);
        }
        this.checker.addListener(this.listener);
        final List<File> sources = this.getNonExcludedFiles(files);
        final Collection<Violation> results = new ArrayList<>(0);
        if (sources.isEmpty()) {
            Logger.debug(
                this,
                "No files to check with Checkstyle, all %d are excluded",
                files.size()
            );
        } else {
            try {
                Logger.debug(this, "Checkstyle processing %d files", sources.size());
                this.checker.process(sources);
                Logger.debug(this, "Checkstyle processed %d files", sources.size());
            } catch (final CheckstyleException ex) {
                throw new IllegalStateException("Failed to process files", ex);
            }
            for (final AuditEvent event : this.listener.events()) {
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
            results.addAll(
                new UnusedSuppressions(this.env).validate(this.listener.processed())
            );
        }
        return results;
    }

    @Override public String name() {
        return "Checkstyle";
    }

    @Override
    public int rules() {
        return CheckstyleValidator.count(this.configuration());
    }

    /**
     * Filters out excluded files from further validation.
     * @param files Files to validate
     * @return List of relevant files
     */
    public List<File> getNonExcludedFiles(final Collection<File> files) {
        final List<File> relevant = new ArrayList<>(files.size());
        for (final File file : files) {
            final String name = new Relative(this.env.basedir(), file).path();
            if (this.env.exclude("checkstyle", name)) {
                continue;
            }
            final int dot = name.lastIndexOf('.');
            if (dot < 0) {
                continue;
            }
            final String ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);
            if (!CheckstyleValidator.EXTENSIONS.contains(ext)) {
                continue;
            }
            relevant.add(file);
        }
        return relevant;
    }

    private Configuration configuration() {
        final File cache =
            new File(this.env.tempdir(), "checkstyle/checkstyle.cache");
        final File parent = cache.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException(
                String.format(
                    "Unable to create directories needed for %s",
                    cache.getPath()
                )
            );
        }
        if (!parent.canWrite()) {
            throw new IllegalStateException(
                String.format(
                    "Cannot write to %s, check filesystem permissions",
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
            config = ConfigurationLoader.loadConfiguration(
                new InputSource(stream),
                new PropertiesExpander(props),
                ConfigurationLoader.IgnoredModulesOptions.OMIT
            );
        } catch (final CheckstyleException | java.io.IOException ex) {
            throw new IllegalStateException("Failed to load config", ex);
        }
        if (this.level() < CheckstyleValidator.MINIMUM) {
            CheckstyleValidator.strip(config, CheckstyleValidator.MODERN);
        }
        return config;
    }

    private int level() {
        int level = CheckstyleValidator.parse(
            this.env.param("maven.compiler.release", "")
        );
        if (level < 0) {
            level = CheckstyleValidator.parse(
                this.env.param("maven.compiler.source", "")
            );
        }
        final int result;
        if (level < 0) {
            result = CheckstyleValidator.MINIMUM - 1;
        } else {
            result = level;
        }
        return result;
    }

    private static int parse(final String value) {
        int result = -1;
        if (value != null) {
            String txt = value.trim();
            if (txt.startsWith("1.")) {
                txt = txt.substring(2);
            }
            try {
                result = Integer.parseInt(txt);
            } catch (final NumberFormatException ex) {
                result = -1;
            }
        }
        return result;
    }

    private static int count(final Configuration config) {
        int total = 0;
        for (final Configuration child : config.getChildren()) {
            final String name = child.getName();
            if (child.getChildren().length > 0) {
                total += CheckstyleValidator.count(child);
            } else if (!name.endsWith("Filter") && !name.endsWith("Holder")) {
                total += 1;
            }
        }
        return total;
    }

    private static void strip(final Configuration config,
        final Set<String> names) {
        for (final Configuration child : config.getChildren()) {
            final String name = child.getName();
            final String simple = name.substring(name.lastIndexOf('.') + 1);
            if (names.contains(simple)) {
                ((DefaultConfiguration) config).removeChild(child);
            } else {
                CheckstyleValidator.strip(child, names);
            }
        }
    }
}
