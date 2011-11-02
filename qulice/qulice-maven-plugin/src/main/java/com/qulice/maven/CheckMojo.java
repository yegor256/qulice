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

import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import com.ymock.util.Logger;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Check the project and find all possible violations.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 * @goal check
 * @phase verify
 * @threadSafe
 */
public final class CheckMojo extends AbstractMojo implements Contextualizable {

    /**
     * Environment to pass to validators.
     */
    private MavenEnvironment env = new MavenEnvironment();

    /**
     * Maven project, to be injected by Maven itself.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Maven session, to be injected by Maven itself.
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * Maven plugin manager, to be injected by Maven itself.
     * @component
     * @required
     */
    private MavenPluginManager manager;

    /**
     * Shall we skip execution?
     * @parameter expression="${qulice.skip}" default-value="false"
     * @required
     * @since 0.1
     */
    private boolean skip;

    /**
     * Location of License file. If it is an absolute file name you should
     * prepend it with "file:" prefix. Otherwise it is treated like a resource
     * name and will be found in classpath (if available).
     * @parameter expression="${qulice.license}" default-value="LICENSE.txt"
     * @required
     * @since 0.1
     */
    private String license = "LICENSE.txt";

    /**
     * Set Maven Project (used mostly for unit testing).
     * @param proj The project to set
     */
    public void setProject(final MavenProject proj) {
        this.project = proj;
    }

    /**
     * Set skip option (mostly for unit testing).
     * @param skp The "skip" option
     */
    public void setSkip(final boolean skp) {
        this.skip = skp;
    }

    /**
     * Set license address.
     * @param lcs The "license" option
     */
    public void setLicense(final String lcs) {
        this.license = lcs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextualize(final Context ctx) {
        this.env.setContext(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoFailureException {
        StaticLoggerBinder.getSingleton().setMavenLog(this.getLog());
        if (this.skip) {
            Logger.info(this, "Execution skipped");
            return;
        }
        this.env.setProperty("license", this.license);
        this.env.setProject(this.project);
        this.env.setMojoExecutor(
            new MojoExecutor(this.manager, this.session)
        );
        final long start = System.nanoTime();
        for (Validator validator : new ValidatorsProvider().all()) {
            try {
                validator.validate(this.env);
            } catch (ValidationException ex) {
                throw new MojoFailureException("Failed", ex);
            }
        }
        // Output elapsed time.
        Logger.info(
            this,
            "Time elapsed on validation: %.2fs",
            // @checkstyle MagicNumber (1 lines)
            (double) (System.nanoTime() - start) / (1000L * 1000 * 1000)
        );
    }
}
