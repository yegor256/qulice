/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.mockito.Mockito;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Mocker of {@link MavenProject}.
 * @since 0.4
 */
@SuppressWarnings("PMD.TooManyMethods")
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
        this.container = Mockito.mock(PlexusContainer.class);
        this.ass = new LinkedList<>();
        this.ienv = new Environment.Mock();
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
        Mockito.doReturn(object).when(this.container).lookup(role, hint);
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
        StaticLoggerBinder.getSingleton().setMavenLog(Mockito.mock(Log.class));
        this.prj.inBasedir(this.ienv.basedir());
        final MavenProject project = this.prj.mock();
        final Environment parent = this.ienv;
        final MavenEnvironment env = Mockito.mock(MavenEnvironment.class);
        Mockito.doReturn(project).when(env).project();
        final Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(env).context();
        Mockito.doReturn(this.container).when(context).get(Mockito.anyString());
        Mockito.doReturn(this.ass).when(env).asserts();
        return new MavenEnvironment.Wrap(parent, env);
    }

}
