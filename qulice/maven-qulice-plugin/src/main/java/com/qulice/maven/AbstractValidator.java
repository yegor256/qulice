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

import java.util.Properties;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Abstract validator.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public abstract class AbstractValidator implements Validator {

    /**
     * Maven project.
     */
    private final MavenProject project;

    /**
     * Maven log.
     */
    private final Log log;

    /**
     * Plugin configuration.
     */
    private final Properties config;

    /**
     * Executor of MOJO-s.
     */
    private MojoExecutor executor;

    /**
     * Public ctor.
     * @param pjct The project we're working in
     * @param mlog The Maven log
     * @param cfg Set of options provided in "configuration" section
     */
    public AbstractValidator(final MavenProject pjct, final Log mlog,
        final Properties cfg) {
        this.project = pjct;
        this.log = mlog;
        this.config = cfg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void validate() throws MojoFailureException;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void inject(final MojoExecutor exec) {
        this.executor = exec;
    }

    /**
     * Get maven project.
     * @return The project
     */
    protected final MavenProject project() {
        return this.project;
    }

    /**
     * Get Maven log.
     * @return The log
     */
    protected final Log log() {
        return this.log;
    }

    /**
     * Get plugin configuration properties.
     * @return The props
     */
    protected final Properties config() {
        return this.config;
    }

    /**
     * Get MOJO executor.
     * @return The executor
     */
    protected final MojoExecutor executor() {
        return this.executor;
    }

}
