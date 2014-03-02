/**
 * Copyright (c) 2011-2013, Qulice.com
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
// @checkstyle LineLength (20 lines)

import com.jcabi.log.Logger;
import com.qulice.spi.ValidationException;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.ContextException;

/**
 * Validator of dependencies.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
final class DependenciesValidator implements MavenValidator {

    /**
     * Separator between lines.
     */
    private static final String SEP = "\n\t";

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (4 lines)
     */
    @Override
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        if (!env.outdir().exists()
            || "pom".equals(env.project().getPackaging())) {
            Logger.info(this, "No dependency analysis in this project");
            return;
        }
        final ProjectDependencyAnalysis analysis = this.analyze(env);
        final Collection<String> unused = new LinkedList<String>();
        for (final Object obj : analysis.getUnusedDeclaredArtifacts()) {
            final Artifact artifact = (Artifact) obj;
            if (!artifact.getScope().equals(Artifact.SCOPE_COMPILE)) {
                continue;
            }
            unused.add(artifact.toString());
        }
        if (!unused.isEmpty()) {
            Logger.warn(
                this,
                "Unused declared dependencies found:%s%s",
                DependenciesValidator.SEP,
                StringUtils.join(unused, DependenciesValidator.SEP)
            );
        }
        final Collection<String> used = new LinkedList<String>();
        for (final Object artifact : analysis.getUsedUndeclaredArtifacts()) {
            used.add(((Artifact) artifact).toString());
        }
        if (!used.isEmpty()) {
            Logger.warn(
                this,
                "Used undeclared dependencies found:%s%s",
                DependenciesValidator.SEP,
                StringUtils.join(used, DependenciesValidator.SEP)
            );
        }
        final Integer failures = used.size() + unused.size();
        if (failures > 0) {
            throw new ValidationException(
                "%d dependency problem(s) found",
                failures
            );
        }
        Logger.info(this, "No dependency problems found");
    }

    /**
     * Analyze the project.
     * @param env The environment
     * @return The result of analysis
     */
    private ProjectDependencyAnalysis analyze(final MavenEnvironment env) {
        try {
            return ((ProjectDependencyAnalyzer)
                ((PlexusContainer)
                    env.context().get(PlexusConstants.PLEXUS_KEY)
                ).lookup(ProjectDependencyAnalyzer.ROLE, "default")
            ).analyze(env.project());
        } catch (ContextException ex) {
            throw new IllegalStateException(ex);
        } catch (ComponentLookupException ex) {
            throw new IllegalStateException(ex);
        } catch (ProjectDependencyAnalyzerException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
