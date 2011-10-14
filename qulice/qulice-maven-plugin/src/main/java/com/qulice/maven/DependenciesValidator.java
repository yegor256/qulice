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

import com.ymock.util.Logger;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * Validator of dependencies.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class DependenciesValidator extends AbstractValidator {

    /**
     * Separator between lines.
     */
    private static final String SEP = "\n\t";

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final Environment env) throws MojoFailureException {
        final File output =
            new File(env.project().getBuild().getOutputDirectory());
        if (!output.exists() || "pom".equals(env.project().getPackaging())) {
            Logger.info(this, "No dependency analysis in this project");
            return;
        }
        final ProjectDependencyAnalysis analysis = this.analyze(env);
        final List<String> unused = new ArrayList<String>();
        for (Object obj : analysis.getUnusedDeclaredArtifacts()) {
            final Artifact artifact = (Artifact) obj;
            if (!artifact.getScope().equals(Artifact.SCOPE_COMPILE)) {
                continue;
            }
            unused.add(artifact.toString());
        }
        if (unused.size() > 0) {
            Logger.warn(
                this,
                "Unused declared dependencies found:%s%s",
                this.SEP,
                StringUtils.join(unused, this.SEP)
            );
        }
        final List<String> used = new ArrayList<String>();
        for (Object artifact : analysis.getUsedUndeclaredArtifacts()) {
            used.add(((Artifact) artifact).toString());
        }
        if (used.size() > 0) {
            Logger.warn(
                this,
                "Used undeclared dependencies found:%s%s",
                this.SEP,
                StringUtils.join(used, this.SEP)
            );
        }
        final Integer failures = used.size() + unused.size();
        if (failures > 0) {
            throw new MojoFailureException(
                String.format(
                    "%d dependency problem(s) found",
                    failures
                )
            );
        }
        Logger.info(this, "No dependency problems found");
    }

    /**
     * Analyze the project.
     * @param env The environment
     * @return The result of analysis
     */
    private ProjectDependencyAnalysis analyze(final Environment env) {
        try {
            return
                ((ProjectDependencyAnalyzer)
                    ((PlexusContainer)
                        env.context().get(PlexusConstants.PLEXUS_KEY)
                    ).lookup(ProjectDependencyAnalyzer.ROLE, "default")
                ).analyze(env.project());
        } catch (org.codehaus.plexus.context.ContextException ex) {
            throw new IllegalStateException(ex);
        } catch (ComponentLookupException ex) {
            throw new IllegalStateException(ex);
        } catch (ProjectDependencyAnalyzerException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
