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
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
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
        checker.process(this.files());
        checker.destroy();
        final List<AuditEvent> events = listener.events();
        if (!events.isEmpty()) {
            for (AuditEvent event : events) {
                final String check = event.getSourceName();
                this.log().error(
                    String.format(
                        "%s[%d]: %s (%s)",
                        event.getFileName().substring(
                            this.project().getBasedir().toString().length()
                        ),
                        event.getLine(),
                        event.getMessage(),
                        check.substring(check.lastIndexOf('.') + 1)
                    )
                );
            }
            throw new MojoFailureException(
                String.format(
                    "%d Checkstyle violations (see log above)",
                    events.size()
                )
            );
        }
    }

    /**
     * Load checkstyle configuration.
     * @return The configuration just loaded
     * @see #validate(MavenProject,Properties)
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
     * @see #validate(MavenProject,Properties)
     */
    private ClassLoader classloader() {
        final List<String> paths = new ArrayList<String>();
        try {
            paths.addAll(this.project().getTestClasspathElements());
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
        return new URLClassLoader(urls.toArray(new URL[] {}));
    }

    /**
     * Get full list of files to process.
     * @throws MojoExecutionException If something goes wrong
     * @see #validate(MavenProject,Properties)
     */
    private List<File> files() {
        final List<File> files = new ArrayList<File>();
        final IOFileFilter filter = new WildcardFileFilter("*.java");
        files.addAll(
            FileUtils.listFiles(
                this.project().getBasedir(),
                filter,
                DirectoryFileFilter.INSTANCE
            )
        );
        return files;
    }

    /**
     * Create header content, from file.
     * @return The content of header
     * @see #configuration(MavenProject,Properties)
     */
    private String header() {
        final String name = this.config().getProperty("license", "/LICENSE.txt");
        File file;
        if (name.startsWith(this.FILE_PREFIX)) {
            file = new File(name.substring(this.FILE_PREFIX.length()));
        } else {
            final URL url = this.classloader().getResource(name);
            if (url == null) {
                throw new IllegalStateException(
                    String.format(
                        "'%s' resource is not found in classpath",
                        name
                    )
                );
            }
            file = new File(url.getFile());
        }
        if (!file.exists()) {
            throw new IllegalStateException(
                String.format(
                    "File '%s' not found",
                    file.getPath()
                )
            );
        }
        String content;
        try {
            content = FileUtils.readFileToString(file).replace("\n", "\\n * ");
        } catch (java.io.IOException ex) {
            throw new IllegalStateException("Failed to read header", ex);
        }
        return "/**\n * " + content + "\n */\n";
    }

    /**
     * Listener of events.
     */
    private static final class Listener implements AuditListener {
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
