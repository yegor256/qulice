/**
 * Copyright (c) 2011-2012, Qulice.com
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
import com.qulice.spi.EnvironmentMocker;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.mockito.Mockito;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Mocker of {@link MavenProject}.
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class MavenEnvironmentMocker {

    /**
     * Env mocker.
     */
    private final transient EnvironmentMocker envMocker;

    /**
     * Project.
     */
    private transient MavenProjectMocker projectMocker =
        new MavenProjectMocker();

    /**
     * Plexus container, mock.
     */
    private final transient PlexusContainer container =
        Mockito.mock(PlexusContainer.class);

    /**
     * Public ctor.
     * @throws IOException If some IO problem inside
     */
    public MavenEnvironmentMocker() throws IOException {
        StaticLoggerBinder.getSingleton().setMavenLog(Mockito.mock(Log.class));
        this.envMocker = new EnvironmentMocker();
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
        this.projectMocker = mocker;
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
        this.envMocker.withFile(name, content);
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
        this.envMocker.withFile(name, bytes);
        return this;
    }

    /**
     * Mock it.
     * @return The environment just mocked
     * @throws Exception If something wrong happens inside
     */
    public MavenEnvironment mock() throws Exception {
        this.projectMocker.inBasedir(this.envMocker.getBasedir());
        final MavenProject project = this.projectMocker.mock();
        final Environment parent = this.envMocker.mock();
        final MavenEnvironment env = Mockito.mock(MavenEnvironment.class);
        Mockito.doReturn(project).when(env).project();
        final Context context = Mockito.mock(Context.class);
        Mockito.doReturn(context).when(env).context();
        Mockito.doReturn(this.container).when(context).get(Mockito.anyString());
        return new EnvWrapper(parent, env);
    }

    /**
     * Wrapper of maven environment.
     */
    @SuppressWarnings("PMD.TooManyMethods")
    private static final class EnvWrapper implements MavenEnvironment {
        /**
         * Parent environment.
         */
        private final transient Environment env;
        /**
         * Parent maven environment.
         */
        private final transient MavenEnvironment menv;
        /**
         * Public ctor.
         * @param penv Parent env
         * @param pmenv Parent maven env
         */
        public EnvWrapper(final Environment penv,
            final MavenEnvironment pmenv) {
            this.env = penv;
            this.menv = pmenv;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public File basedir() {
            return this.env.basedir();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public File tempdir() {
            return this.env.tempdir();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public File outdir() {
            return this.env.outdir();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public String param(final String name, final String value) {
            return this.env.param(name, value);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public ClassLoader classloader() {
            return this.env.classloader();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Collection<File> classpath() {
            return this.env.classpath();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public MavenProject project() {
            return this.menv.project();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Properties properties() {
            return this.menv.properties();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Context context() {
            return this.menv.context();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Properties config() {
            return this.menv.config();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public MojoExecutor executor() {
            return this.menv.executor();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Collection<File> files(final String pattern) {
            throw new UnsupportedOperationException();
        }
    }

}
