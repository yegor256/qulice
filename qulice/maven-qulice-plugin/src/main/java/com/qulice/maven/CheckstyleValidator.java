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
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
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
     * {@inheritDoc}
     */
    @Override
    public void validate(final MavenProject project, final Properties config)
        throws MojoExecutionException {
        Checker checker;
        try {
            checker = new Checker();
        } catch (CheckstyleException ex) {
            throw new MojoExecutionException("Failed to create checker", ex);
        }
        final InputSource src = new InputSource(
            this.getClass().getResourceAsStream("checkstyle/checks.xml")
        );
        checker.setClassloader(this.classloader(project));
        checker.setModuleClassLoader(this.getClass().getClassLoader());
        try {
            checker.configure(this.configuration(project, config));
        } catch (CheckstyleException ex) {
            throw new MojoExecutionException("Failed to configure checker", ex);
        }
        checker.process(this.files(project));
        checker.destroy();
    }

    /**
     * Load checkstyle configuration.
     * @param project The project
     * @param config Configuration of plugin
     * @return The configuration just loaded
     * @see #validate(MavenProject,Properties)
     */
    private Configuration configuration(final MavenProject project,
        final Properties config) throws MojoExecutionException {
        final Properties props = new Properties();
        final File buildDir = new File(
            project.getProperties().getProperty("project.build.directory")
        );
        props.setProperty(
            "cache.file",
            new File(buildDir, "qulice-checkstyle.cache").getPath()
        );
        props.setProperty("header.file", this.license(project, config));
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
            throw new MojoExecutionException("Failed to load config", ex);
        }
        return configuration;
    }

    /**
     * Create classloader for checkstyle.
     * @param project The project
     * @return The classloader
     * @see #validate(MavenProject,Properties)
     */
    private ClassLoader classloader(final MavenProject project)
        throws MojoExecutionException {
        List<String> paths;
        try {
            paths = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Failed to read classpath", ex);
        }
        paths.add(project.getBuild().getOutputDirectory());
        paths.add(project.getBuild().getTestOutputDirectory());
        final List<URL> urls = new ArrayList<URL>();
        for (String path : paths) {
            try {
                urls.add(new File(path).toURI().toURL());
            } catch (java.net.MalformedURLException ex) {
                throw new MojoExecutionException("Failed to build URL", ex);
            }
        }
        return new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), null);
    }

    /**
     * Get full list of files to process.
     * @param project The project to process
     * @throws MojoExecutionException If something goes wrong
     * @see #validate(MavenProject,Properties)
     */
    private List<File> files(final MavenProject project)
        throws MojoExecutionException {
        return new ArrayList<File>();
    }

    /**
     * Find license file.
     * @param project The project
     * @param config Configuration of plugin
     * @return The file absolute path
     * @see #configuration(MavenProject,Properties)
     */
    private String license(final MavenProject project,
        final Properties config) throws MojoExecutionException {
        final String name = config.getProperty(
            "license",
            "/checkstyle/LICENSE.txt"
        );
        File file;
        if (name.startsWith(this.FILE_PREFIX)) {
            file = new File(name.substring(this.FILE_PREFIX.length()));
        } else {
            final URL license = this.getClass().getResource(name);
            if (license == null) {
                throw new MojoExecutionException(
                    String.format(
                        "'%s' resource is not found in classpath",
                        name
                    )
                );
            }
            file = new File(license.getFile());
        }
        if (!file.exists()) {
            throw new MojoExecutionException(
                String.format(
                    "File '%s' not found",
                    file.getPath()
                )
            );
        }
        System.out.println(file);
        return file.getPath();
    }

}
