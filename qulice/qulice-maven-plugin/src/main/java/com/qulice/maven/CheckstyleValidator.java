/**
 * Copyright (c) 2011, Qulice.com
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
package com.qulice.maven;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertyResolver;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.xml.sax.InputSource;

/**
 * Validator with Checkstyle.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class CheckstyleValidator extends AbstractValidator {

    /**
     * Prefix to use before files.
     */
    private static final String FILE_PREFIX = "file:";

    /**
     * Public ctor.
     * @param project The project we're working in
     * @param log The Maven log
     * @param config Set of options provided in "configuration" section
     */
    public CheckstyleValidator(final MavenProject project, final Log log,
        final Properties config) {
        super(project, log, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() throws MojoFailureException {
        final List<File> files = this.files();
        if (files.isEmpty()) {
            this.log().info("No files to check with Checkstyle");
            return;
        }
        Checker checker;
        try {
            checker = new Checker();
        } catch (CheckstyleException ex) {
            throw new IllegalStateException("Failed to create checker", ex);
        }
        checker.setClassloader(this.classloader());
        checker.setModuleClassLoader(this.getClass().getClassLoader());
        try {
            checker.configure(this.configuration());
        } catch (CheckstyleException ex) {
            throw new IllegalStateException("Failed to configure checker", ex);
        }
        final Listener listener = new Listener();
        checker.addListener(listener);
        checker.process(files);
        checker.destroy();
        final List<AuditEvent> events = listener.events();
        if (!events.isEmpty()) {
            throw new MojoFailureException(
                String.format(
                    "%d Checkstyle violations (see log above)",
                    events.size()
                )
            );
        }
        this.log().info(
            String.format(
                "No Checkstyle violations found in %d files",
                files.size()
            )
        );
    }

    /**
     * Load checkstyle configuration.
     * @return The configuration just loaded
     * @see #validate()
     */
    private Configuration configuration() {
        final File buildDir = new File(
            this.project().getBuild().getOutputDirectory()
        );
        final Properties props = new Properties();
        props.setProperty(
            "cache.file",
            new File(buildDir, "qulice-checkstyle.cache").getPath()
        );
        props.setProperty("header", this.header());
        final InputSource src = new InputSource(
            this.getClass().getResourceAsStream("checkstyle/checks.xml")
        );
        Configuration configuration;
        try {
            configuration = ConfigurationLoader.loadConfiguration(
                src,
                new PropertiesExpander(props),
                // omit ignored modules
                true
            );
        } catch (CheckstyleException ex) {
            throw new IllegalStateException("Failed to load config", ex);
        }
        return configuration;
    }

    /**
     * Create classloader for checkstyle.
     * @return The classloader
     * @see #validate()
     */
    private ClassLoader classloader() {
        final List<String> paths = new ArrayList<String>();
        try {
            paths.addAll(this.project().getRuntimeClasspathElements());
        } catch (DependencyResolutionRequiredException ex) {
            throw new IllegalStateException("Failed to read classpath", ex);
        }
        final List<URL> urls = new ArrayList<URL>();
        for (String path : paths) {
            try {
                urls.add(new File(path).toURI().toURL());
            } catch (java.net.MalformedURLException ex) {
                throw new IllegalStateException("Failed to build URL", ex);
            }
        }
        final URLClassLoader loader =
            new URLClassLoader(urls.toArray(new URL[] {}), this.getClass().getClassLoader());
        for (URL url : loader.getURLs()) {
            this.log().debug("Classpath: " + url);
        }
        return loader;
    }

    /**
     * Create header content, from file.
     * @return The content of header
     * @see #configuration()
     */
    private String header() {
        final String name = this.config().getProperty("license", "LICENSE.txt");
        URL url;
        if (name.startsWith(this.FILE_PREFIX)) {
            try {
                url = new URL(name);
            } catch (java.net.MalformedURLException ex) {
                throw new IllegalStateException("Invalid URL", ex);
            }
        } else {
            url = this.classloader().getResource(name);
            if (url == null) {
                throw new IllegalStateException(
                    String.format(
                        "'%s' resource is not found in classpath",
                        name
                    )
                );
            }
        }
        String content;
        try {
            content = IOUtils.toString(url.openStream());
        } catch (java.io.IOException ex) {
            throw new IllegalStateException("Failed to read header", ex);
        }
        final StringBuilder builder = new StringBuilder();
        builder.append("/**\n");
        for (String line : StringUtils.splitPreserveAllTokens(content, '\n')) {
            if (line.length() > 0) {
                builder.append(" * " + line);
            } else {
                builder.append(" *");
            }
            builder.append("\n");
        }
        builder.append(" */\n");
        final String license = builder.toString();
        this.log().info("LICENSE found: " + url);
        this.log().debug(license);
        return license;
    }

    /**
     * Listener of events.
     */
    private final class Listener implements AuditListener {
        /**
         * Collection of events collected.
         */
        private final List<AuditEvent> events = new ArrayList<AuditEvent>();
        /**
         * Get all events.
         */
        public List<AuditEvent> events() {
            return this.events;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void auditStarted(final AuditEvent event) {
            // intentionally empty
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void auditFinished(final AuditEvent event) {
            // intentionally empty
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void fileStarted(final AuditEvent event) {
            // intentionally empty
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void fileFinished(final AuditEvent event) {
            // intentionally empty
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void addError(final AuditEvent event) {
            this.events.add(event);
            final String check = event.getSourceName();
            CheckstyleValidator.this.log().error(
                String.format(
                    "%s[%d]: %s (%s)",
                    event.getFileName().substring(
                        CheckstyleValidator.this.project().getBasedir()
                            .toString().length()
                    ),
                    event.getLine(),
                    event.getMessage(),
                    check.substring(check.lastIndexOf('.') + 1)
                )
            );
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void addException(final AuditEvent event, Throwable throwable) {
            // intentionally empty
        }
    }

}
