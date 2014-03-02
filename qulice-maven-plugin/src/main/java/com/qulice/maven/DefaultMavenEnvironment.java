/**
 * Copyright (c) 2011-2013, Qulice.com
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

import com.jcabi.log.Logger;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.Context;

/**
 * Environment, passed from MOJO to validators.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class DefaultMavenEnvironment implements MavenEnvironment {

    /**
     * Maven project.
     */
    private transient MavenProject iproject;

    /**
     * Plexus context.
     */
    private transient Context icontext;

    /**
     * Plugin configuration.
     */
    private final transient Properties iproperties = new Properties();

    /**
     * MOJO executor.
     */
    private transient MojoExecutor exectr;

    /**
     * Excludes, regular expressions.
     */
    private final transient Collection<String> exc =
        new LinkedList<String>();

    @Override
    public String param(final String name, final String value) {
        final String val = this.iproperties.getProperty(name);
        String ret = val;
        if (ret == null) {
            ret = value;
        }
        return ret;
    }

    @Override
    public File basedir() {
        return this.iproject.getBasedir();
    }

    @Override
    public File tempdir() {
        return new File(this.iproject.getBuild().getOutputDirectory());
    }

    @Override
    public File outdir() {
        return new File(this.iproject.getBuild().getOutputDirectory());
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Collection<File> classpath() {
        final Collection<File> paths = new ArrayList<File>();
        try {
            for (final String name
                : this.iproject.getRuntimeClasspathElements()) {
                paths.add(new File(name));
            }
            for (final Artifact artifact
                : this.iproject.getDependencyArtifacts()) {
                paths.add(artifact.getFile());
            }
        } catch (DependencyResolutionRequiredException ex) {
            throw new IllegalStateException("Failed to read classpath", ex);
        }
        return paths;
    }

    @Override
    public ClassLoader classloader() {
        final List<URL> urls = new ArrayList<URL>();
        for (final File path : this.classpath()) {
            try {
                urls.add(path.toURI().toURL());
            } catch (java.net.MalformedURLException ex) {
                throw new IllegalStateException("Failed to build URL", ex);
            }
        }
        final URLClassLoader loader = new URLClassLoader(
            urls.toArray(new URL[] {}),
            Thread.currentThread().getContextClassLoader()
        );
        for (final URL url : loader.getURLs()) {
            Logger.debug(this, "Classpath: %s", url);
        }
        return loader;
    }

    @Override
    public MavenProject project() {
        return this.iproject;
    }

    @Override
    public Properties properties() {
        return this.iproperties;
    }

    @Override
    public Context context() {
        return this.icontext;
    }

    @Override
    public Properties config() {
        return this.iproperties;
    }

    @Override
    public MojoExecutor executor() {
        return this.exectr;
    }

    @Override
    public Collection<File> files(final String pattern) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean exclude(final String check, final String name) {
        final String line = String.format("%s:%s", check, name);
        boolean exclude = false;
        for (final String expr : this.exc) {
            if (line.matches(expr)) {
                exclude = true;
                break;
            }
        }
        return exclude;
    }

    /**
     * Set Maven Project (used mostly for unit testing).
     * @param proj The project to set
     */
    public void setProject(final MavenProject proj) {
        this.iproject = proj;
    }

    /**
     * Set context.
     * @param ctx The context to set
     */
    public void setContext(final Context ctx) {
        this.icontext = ctx;
    }

    /**
     * Set executor.
     * @param exec The executor
     */
    public void setMojoExecutor(final MojoExecutor exec) {
        this.exectr = exec;
    }

    /**
     * Set property.
     * @param name Its name
     * @param value Its value
     */
    public void setProperty(final String name, final String value) {
        this.iproperties.setProperty(name, value);
    }

    /**
     * Set list of regular expressions to exclude.
     * @param exprs Expressions
     */
    public void setExcludes(final Collection<String> exprs) {
        this.exc.clear();
        this.exc.addAll(exprs);
    }

}
