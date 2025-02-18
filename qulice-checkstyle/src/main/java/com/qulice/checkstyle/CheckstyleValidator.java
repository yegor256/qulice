/*
 * Copyright (c) 2011-2025 Yegor Bugayenko
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.checkstyle;

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
            this.checker.process(sources);
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
            if (!name.matches("^.*\\.java$")) {
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
                    "Unable to create directories needed for %s",
                    cache.getPath()
                )
            );
        }
        final Properties props = new Properties();
        props.setProperty("cache.file", cache.getPath());
        final InputSource src = new InputSource(
            this.getClass().getResourceAsStream("checks.xml")
        );
        final Configuration config;
        try {
            config = ConfigurationLoader.loadConfiguration(
                src,
                new PropertiesExpander(props),
                ConfigurationLoader.IgnoredModulesOptions.OMIT
            );
        } catch (final CheckstyleException ex) {
            throw new IllegalStateException("Failed to load config", ex);
        }
        return config;
    }
}
