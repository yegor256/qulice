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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Check the project and find all possible violations.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 * @goal check
 * @phase verify
 * @threadSafe
 */
public final class CheckMojo extends AbstractMojo {

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
     */
    private boolean skip;

    /**
     * Licence file location.
     * @parameter expression="${qulice.license}" default-value="LICENSE.txt"
     * @required
     */
    private String license;

    /**
     * Set Maven Project (used mostly for unit testing).
     * @param proj The project to set
     */
    public final void setProject(final MavenProject proj) {
        this.project = proj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute() throws MojoFailureException {
        if (this.skip) {
            this.getLog().info("Execution skipped");
            return;
        }
        final Properties props = new Properties();
        props.setProperty("license", this.license);
        final List<Validator> validators = new ArrayList<Validator>();
        validators.add(
            new EnforcerValidator(project, this.getLog(), props)
        );
        validators.add(
            new DependenciesValidator(project, this.getLog(), props)
        );
        validators.add(
            new XmlValidator(project, this.getLog(), props)
        );
        validators.add(
            new CheckstyleValidator(project, this.getLog(), props)
        );
        validators.add(
            new PMDValidator(project, this.getLog(), props)
        );
        validators.add(
            new FindBugsValidator(project, this.getLog(), props)
        );
        // not working yet
        // validators.add(
        //     new CoberturaValidator(project, this.getLog(), props)
        // );
        final MojoExecutor exec = new MojoExecutor(
            this.manager, this.session, this.getLog()
        );
        for (Validator validator : validators) {
            validator.inject(exec);
            validator.validate();
        }
    }

}
