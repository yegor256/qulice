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

import com.qulice.spi.Environment;
import com.ymock.util.Logger;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.Context;

/**
 * Environment, passed from MOJO to validators.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class MavenEnvironment implements Environment {

    /**
     * Maven project.
     */
    private MavenProject project;

    /**
     * Plexus context.
     */
    private Context context;

    /**
     * Plugin configuration.
     */
    private final Properties properties = new Properties();

    /**
     * MOJO executor.
     */
    private MojoExecutor mojoExecutor;

    /**
     * {@inheritDoc}
     */
    @Override
    public String param(final String name, final String value) {
        final String val = this.properties.getProperty(name);
        if (val == null) {
            return value;
        }
        return val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File basedir() {
        return this.project.getBasedir();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File tempdir() {
        return new File(this.project.getBuild().getOutputDirectory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File outdir() {
        return new File(this.project.getBuild().getOutputDirectory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<File> classpath() {
        final Collection<File> paths = new ArrayList<File>();
        try {
            for (String name : this.project.getRuntimeClasspathElements()) {
                paths.add(new File(name));
            }
        } catch (DependencyResolutionRequiredException ex) {
            throw new IllegalStateException("Failed to read classpath", ex);
        }
        return paths;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassLoader classloader() {
        final List<URL> urls = new ArrayList<URL>();
        for (File path : this.classpath()) {
            try {
                urls.add(path.toURI().toURL());
            } catch (java.net.MalformedURLException ex) {
                throw new IllegalStateException("Failed to build URL", ex);
            }
        }
        final URLClassLoader loader = new URLClassLoader(
            urls.toArray(new URL[] {}),
            this.getClass().getClassLoader()
        );
        for (URL url : loader.getURLs()) {
            Logger.debug(this, "Classpath: %s", url);
        }
        return loader;
    }

    /**
     * Set Maven Project (used mostly for unit testing).
     * @param proj The project to set
     */
    public void setProject(final MavenProject proj) {
        this.project = proj;
    }

    /**
     * Set context.
     * @param ctx The context to set
     */
    public void setContext(final Context ctx) {
        this.context = ctx;
    }

    /**
     * Set executor.
     * @param exec The executor
     */
    public void setMojoExecutor(final MojoExecutor exec) {
        this.mojoExecutor = exec;
    }

    /**
     * Set property.
     * @param name Its name
     * @param value Its value
     */
    public void setProperty(final String name, final String value) {
        this.properties.setProperty(name, value);
    }

    /**
     * Get project.
     * @return The project
     */
    public MavenProject project() {
        return this.project;
    }

    /**
     * Get properties.
     * @return The properties
     */
    public Properties properties() {
        return this.properties;
    }

    /**
     * Get context.
     * @return The context
     */
    public Context context() {
        return this.context;
    }

    /**
     * Get plugin configuration properties.
     * @return The props
     */
    public Properties config() {
        return this.properties;
    }

    /**
     * Get MOJO executor.
     * @return The executor
     */
    public MojoExecutor executor() {
        return this.mojoExecutor;
    }

}
