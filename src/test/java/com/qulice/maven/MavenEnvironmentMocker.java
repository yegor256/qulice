/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Mocker of {@link MavenProject}.
 * @since 0.4
 */
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
        this.ass = new ArrayList<>(0);
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
        return new MavenEnvironment.Wrap(
            this.ienv,
            new FakeMavenEnvironment(
                this.prj.mock(),
                new FakeContext(this.container),
                this.ass
            )
        );
    }
}
