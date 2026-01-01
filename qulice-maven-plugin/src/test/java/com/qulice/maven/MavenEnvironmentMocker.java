/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Mocker of {@link MavenProject}.
 * @since 0.4
 */
@SuppressWarnings({
    "PMD.TooManyMethods",
    "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
})
public final class MavenEnvironmentMocker {

    /**
     * Env mocker.
     */
    private final Environment.Mock ienv;

    /**
     * Project.
     */
    private MavenProjectMocker prj;

    /**
     * Plexus container, mock.
     */
    private final PlexusContainer container;

    /**
     * Xpath queries to test pom.xml.
     */
    private Collection<String> ass;

    /**
     * Public ctor.
     * @throws IOException If some IO problem inside
     */
    public MavenEnvironmentMocker() throws IOException {
        this.prj = new MavenProjectMocker();
        this.ass = new LinkedList<>();
        this.ienv = new Environment.Mock();
        try {
            this.container = new DefaultPlexusContainer();
        } catch (final PlexusContainerException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Inject this object into plexus container.
     * @param role The role
     * @param hint The hint
     * @param object The object to return
     * @return This object
     * @throws Exception If something wrong happens inside
     */
    public MavenEnvironmentMocker inPlexus(final String role, final String hint,
        final Object object) throws Exception {
        this.container.addComponent(object, role);
        return this;
    }

    /**
     * With this project mocker.
     * @param mocker The project mocker
     * @return This object
     */
    public MavenEnvironmentMocker with(final MavenProjectMocker mocker) {
        this.prj = mocker;
        return this;
    }

    /**
     * With this file on board.
     * @param name File name related to basedir
     * @param content File content to write
     * @return This object
     * @throws IOException If some IO problem
     */
    public MavenEnvironmentMocker withFile(final String name,
        final String content) throws IOException {
        this.ienv.withFile(name, content);
        return this;
    }

    /**
     * With this file on board.
     * @param name File name related to basedir
     * @param bytes File content to write
     * @return This object
     * @throws IOException If some IO problem
     */
    public MavenEnvironmentMocker withFile(final String name,
        final byte[] bytes) throws IOException {
        this.ienv.withFile(name, bytes);
        return this;
    }

    /**
     * With list of xpath queries to validate pom.xml.
     * @param asserts Collection of xpath queries
     * @return This object
     */
    public MavenEnvironmentMocker withAsserts(
        final Collection<String> asserts) {
        this.ass = Collections.unmodifiableCollection(asserts);
        return this;
    }

    /**
     * Mock it.
     * @return The environment just mocked
     * @throws Exception If something wrong happens inside
     */
    public MavenEnvironment mock() throws Exception {
        StaticLoggerBinder.getSingleton().setMavenLog(
            new DefaultLog(
                new ConsoleLogger()
            )
        );
        this.prj.inBasedir(this.ienv.basedir());
        final MavenProject project = this.prj.mock();
        final MavenEnvironment env = new FakeMavenEnvironment(
            project,
            new FakeContext(this.container),
            this.ass
        );
        return new MavenEnvironment.Wrap(this.ienv, env);
    }

    /**
     * FakeContext.
     * A mock to a context.
     *
     * @since 0.24.1
     */
    private static final class FakeContext implements Context {
        /**
         * Container.
         */
        private final PlexusContainer container;

        FakeContext(final PlexusContainer ctainer) {
            this.container = ctainer;
        }

        @Override
        public boolean contains(final Object obj) {
            return true;
        }

        @Override
        public void put(final Object obja, final Object objb) {
            // Intentionally left blank
        }

        @Override
        public Object get(final Object obj) throws ContextException {
            return this.container;
        }

        @Override
        public Map<Object, Object> getContextData() {
            return Collections.emptyMap();
        }
    }

    /**
     * FakeMavenEnvironment.
     * A mock to MavenEnvironment.
     *
     * @since 0.24.1
     */
    private static final class FakeMavenEnvironment implements MavenEnvironment {
        /**
         * Project.
         */
        private final MavenProject proj;

        /**
         * Context.
         */
        private final Context ctx;

        /**
         * Asserts.
         */
        private final Collection<String> assrts;

        FakeMavenEnvironment(
            final MavenProject prj,
            final Context ctx,
            final Collection<String> asserts
        ) {
            this.proj = prj;
            this.ctx = ctx;
            this.assrts = asserts;
        }

        @Override
        public MavenProject project() {
            return this.proj;
        }

        @Override
        public Properties properties() {
            throw new UnsupportedOperationException("#properties()");
        }

        @Override
        public Context context() {
            return this.ctx;
        }

        @Override
        public Properties config() {
            throw new UnsupportedOperationException("#config()");
        }

        @Override
        public MojoExecutor executor() {
            throw new UnsupportedOperationException("#executor()");
        }

        @Override
        public Collection<String> asserts() {
            return this.assrts;
        }

        @Override
        public File basedir() {
            return this.proj.getBasedir();
        }

        @Override
        public File tempdir() {
            return new File("/tmp");
        }

        @Override
        public File outdir() {
            return this.proj.getBasedir();
        }

        @Override
        public String param(final String name, final String value) {
            return "";
        }

        @Override
        public ClassLoader classloader() {
            return ClassLoader.getSystemClassLoader();
        }

        @Override
        public Collection<String> classpath() {
            return Collections.emptyList();
        }

        @Override
        public Collection<File> files(final String pattern) {
            return Collections.emptyList();
        }

        @Override
        public boolean exclude(final String check, final String name) {
            return true;
        }

        @Override
        public Collection<String> excludes(final String checker) {
            return Collections.emptyList();
        }

        @Override
        public Charset encoding() {
            return StandardCharsets.UTF_8;
        }
    }
}
