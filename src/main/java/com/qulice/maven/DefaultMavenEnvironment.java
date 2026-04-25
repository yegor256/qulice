/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.jcabi.log.Logger;
import com.qulice.spi.Binary;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.Context;

/**
 * Environment, passed from MOJO to validators.
 * @since 0.3
 * @checkstyle ClassDataAbstractionCouplingCheck (300 lines)
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public final class DefaultMavenEnvironment implements MavenEnvironment {

    /**
     * Maven project.
     */
    private MavenProject iproject;

    /**
     * Plexus context.
     */
    private Context icontext;

    /**
     * Plugin configuration.
     */
    private final Properties iproperties = new Properties();

    /**
     * MOJO executor.
     */
    private MojoExecutor exectr;

    /**
     * Excludes, regular expressions.
     */
    private final Collection<String> exc = new LinkedList<>();

    /**
     * Xpath queries for pom.xml validation.
     */
    private final Collection<String> assertion = new LinkedList<>();

    /**
     * Source code encoding charset.
     */
    private String charset = "UTF-8";

    @Override
    public String param(final String name, final String value) {
        String ret = this.iproperties.getProperty(name);
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
    @SuppressWarnings("deprecation")
    public Collection<String> classpath() {
        final Collection<String> paths = new LinkedList<>();
        final String blank = "%20";
        final String whitespace = " ";
        try {
            for (final String name
                : this.iproject.getRuntimeClasspathElements()) {
                paths.add(
                    name.replace(
                        File.separatorChar, '/'
                    ).replaceAll(whitespace, blank)
                );
            }
            for (final Artifact artifact
                : this.iproject.getDependencyArtifacts()) {
                if (artifact.getFile() != null) {
                    paths.add(
                        artifact.getFile().getAbsolutePath()
                            .replace(File.separatorChar, '/')
                            .replaceAll(whitespace, blank)
                    );
                }
            }
        } catch (final DependencyResolutionRequiredException ex) {
            throw new IllegalStateException("Failed to read classpath", ex);
        }
        return paths;
    }

    @Override
    public ClassLoader classloader() {
        final List<URL> urls = new LinkedList<>();
        for (final String path : this.classpath()) {
            try {
                urls.add(
                    URI.create(String.format("file:///%s", path)).toURL()
                );
            } catch (final MalformedURLException ex) {
                throw new IllegalStateException("Failed to build URL", ex);
            }
        }
        final URLClassLoader loader =
            new DefaultMavenEnvironment.PrivilegedClassLoader(urls).run();
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
    public Collection<String> asserts() {
        return this.assertion;
    }

    @Override
    public Collection<File> files(final String pattern) {
        final Collection<File> files = new LinkedList<>();
        final IOFileFilter filter = WildcardFileFilter.builder().setWildcards(pattern).get();
        for (final File sources : this.sources()) {
            if (sources.exists()) {
                for (final File found : FileUtils.listFiles(
                    sources,
                    filter,
                    DirectoryFileFilter.INSTANCE
                )) {
                    if (new Binary(found).yes()) {
                        Logger.debug(
                            this,
                            "Skipping binary file %s",
                            found
                        );
                    } else {
                        files.add(found);
                    }
                }
            }
        }
        return files;
    }

    @Override
    public boolean exclude(final String check, final String name) {
        return Iterables.any(
            this.excludes(check),
            new DefaultMavenEnvironment.PathPredicate(name)
        );
    }

    @Override
    public Collection<String> excludes(final String checker) {
        return Collections2.filter(
            Collections2.transform(
                this.exc,
                new DefaultMavenEnvironment.CheckerExcludes(checker)
            ),
            Predicates.notNull()
        );
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

    /**
     * Set list of Xpath queries for pom.xml validation.
     * @param ass Xpath queries
     */
    public void setAssertion(final Collection<String> ass) {
        this.assertion.clear();
        this.assertion.addAll(ass);
    }

    public void setEncoding(final String encoding) {
        this.charset = encoding;
    }

    @Override
    public Charset encoding() {
        if (this.charset == null || this.charset.isEmpty()) {
            this.charset = "UTF-8";
        }
        return Charset.forName(this.charset);
    }

    /**
     * Collect source directories declared by the Maven project.
     *
     * <p>Uses compile and test source roots together with declared resources
     * so that a project configuring {@code <sourceDirectory>} or
     * {@code <testSourceDirectory>} in its POM is honored. Falls back to
     * {@code src} under the basedir when the project exposes no directories,
     * which preserves the historical behavior for minimal stubs. Roots that
     * live under the project's build directory (e.g.
     * {@code target/generated-sources/...}) are filtered out, since those
     * are generated build outputs and not user-authored code (issue #1560).
     * </p>
     *
     * @return Absolute directories to scan for files
     */
    private Collection<File> sources() {
        final Collection<File> dirs = new LinkedList<>();
        final Build build = this.iproject.getBuild();
        final File output = this.buildDirectory(build);
        this.addRoots(dirs, this.iproject.getCompileSourceRoots(), output);
        this.addRoots(dirs, this.iproject.getTestCompileSourceRoots(), output);
        if (build != null) {
            this.addResources(dirs, build.getResources(), output);
            this.addResources(dirs, build.getTestResources(), output);
        }
        if (dirs.isEmpty()) {
            dirs.add(new File(this.basedir(), "src"));
        }
        return dirs;
    }

    /**
     * Resolve the project's build directory.
     * @param build Build descriptor, may be null
     * @return Canonical build directory, or null if not declared
     */
    @Nullable
    private File buildDirectory(@Nullable final Build build) {
        File dir = null;
        if (build != null && build.getDirectory() != null) {
            dir = this.resolve(build.getDirectory());
        }
        return dir;
    }

    /**
     * Add resolved roots to the given collection.
     * @param dirs Collection to fill
     * @param roots Source roots, may be null
     * @param output Build output directory, may be null
     */
    private void addRoots(final Collection<File> dirs,
        final List<String> roots, @Nullable final File output) {
        if (roots != null) {
            for (final String root : roots) {
                final File resolved = this.resolve(root);
                if (DefaultMavenEnvironment.outside(resolved, output)) {
                    dirs.add(resolved);
                } else {
                    Logger.debug(
                        this,
                        "Skipping generated source root %s under %s",
                        resolved, output
                    );
                }
            }
        }
    }

    /**
     * Add resolved resource directories to the given collection.
     * @param dirs Collection to fill
     * @param resources Resources, may be null
     * @param output Build output directory, may be null
     */
    private void addResources(final Collection<File> dirs,
        final List<Resource> resources, @Nullable final File output) {
        if (resources != null) {
            for (final Resource res : resources) {
                final File resolved = this.resolve(res.getDirectory());
                if (DefaultMavenEnvironment.outside(resolved, output)) {
                    dirs.add(resolved);
                } else {
                    Logger.debug(
                        this,
                        "Skipping generated resource directory %s under %s",
                        resolved, output
                    );
                }
            }
        }
    }

    /**
     * Check that the file does not live inside the given parent.
     * @param file Candidate
     * @param parent Possibly enclosing directory, may be null
     * @return True when the file is outside the parent
     */
    private static boolean outside(final File file,
        @Nullable final File parent) {
        boolean answer = true;
        if (parent != null) {
            final String head = FilenameUtils.normalize(
                parent.getAbsolutePath(), true
            );
            final String tail = FilenameUtils.normalize(
                file.getAbsolutePath(), true
            );
            if (head != null && tail != null
                && (tail.equals(head) || tail.startsWith(head.concat("/")))) {
                answer = false;
            }
        }
        return answer;
    }

    /**
     * Resolve a directory path against the project basedir.
     * @param path Absolute or relative path
     * @return Absolute file
     */
    private File resolve(final String path) {
        final File file = new File(path);
        final File resolved;
        if (file.isAbsolute()) {
            resolved = file;
        } else {
            resolved = new File(this.basedir(), path);
        }
        return resolved;
    }

    /**
     * Creates URL ClassLoader in privileged block.
     * @since 0.1
     */
    private static final class PrivilegedClassLoader implements
        PrivilegedAction<URLClassLoader> {

        /**
         * URLs for class loading.
         */
        private final List<URL> urls;

        /**
         * Constructor.
         * @param urls URLs for class loading
         */
        private PrivilegedClassLoader(final List<URL> urls) {
            this.urls = urls;
        }

        @Override
        public URLClassLoader run() {
            return new URLClassLoader(
                this.urls.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader()
            );
        }
    }

    /**
     * Checks if two paths are equal.
     * @since 0.1
     */
    private static class PathPredicate implements Predicate<String> {

        /**
         * Path to match.
         */
        private final String name;

        /**
         * Constructor.
         * @param name Path to match
         */
        PathPredicate(final String name) {
            this.name = name;
        }

        @Override
        public boolean apply(@Nullable final String input) {
            return input != null
                && FilenameUtils.normalize(this.name, true).matches(input);
        }
    }

    /**
     * Converts a checker exclude into exclude param.
     *
     * E.g. "checkstyle:.*" will become ".*".
     *
     * @since 0.1
     */
    private static class CheckerExcludes implements Function<String, String> {

        /**
         * All checkers.
         */
        private static final String ALL = "*";

        /**
         * Name of checker.
         */
        private final String checker;

        /**
         * Constructor.
         * @param checker Name of checker
         */
        CheckerExcludes(final String checker) {
            this.checker = checker;
        }

        @Nullable
        @Override
        public String apply(@Nullable final String input) {
            String result = null;
            if (input != null) {
                final String[] exclude = input.split(":", 2);
                final String check = exclude[0];
                final boolean appropriate = CheckerExcludes.ALL.equals(check)
                    || this.checker.equals(check);
                if (appropriate && exclude.length > 1) {
                    result = exclude[1];
                }
            }
            return result;
        }
    }
}
