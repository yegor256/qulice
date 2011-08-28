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

import java.io.File;
import java.util.Properties;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Validator with PMD.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class PMDValidator extends AbstractValidator {

    /**
     * Public ctor.
     * @param project The project we're working in
     * @param log The Maven log
     * @param config Set of options provided in "configuration" section
     */
    public PMDValidator(final MavenProject project, final Log log,
        final Properties config) {
        super(project, log, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() throws MojoFailureException {
        final Properties props = new Properties();
        props.put("targetJdk", "1.6");
        props.put("includeTests", "true");
        props.put("rulesets", new String[] { "pmd/ruleset.xml" });
        props.put("targetDirectory",
            this.project().getBuild().getOutputDirectory());
        props.put("outputDirectory",
            this.project().getBuild().getOutputDirectory());
        props.put("compileSourceRoots",
            this.project().getCompileSourceRoots());
        props.put("testSourceRoots",
            this.project().getTestCompileSourceRoots());
        this.executor().execute(
            "org.apache.maven.plugins:maven-pmd-plugin:2.5",
            "pmd",
            props
        );
    }

}
