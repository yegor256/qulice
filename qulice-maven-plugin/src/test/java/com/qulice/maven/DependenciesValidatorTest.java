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

import com.qulice.spi.ValidationException;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link DependenciesValidator} class.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class DependenciesValidatorTest {

    /**
     * Plexus role.
     */
    private static final String ROLE = ProjectDependencyAnalyzer.ROLE;

    /**
     * Plexus hint.
     */
    private static final String HINT = "default";

    /**
     * DependencyValidator can pass on when no violations are found.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void passesIfNoDependencyProblemsFound() throws Exception {
        final ProjectDependencyAnalysis analysis =
            Mockito.mock(ProjectDependencyAnalysis.class);
        final ProjectDependencyAnalyzer analyzer = this.analyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(env);
    }

    /**
     * DependencyValidator can catch dependency problems.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = ValidationException.class)
    public void catchesDependencyProblemsAndThrowsException() throws Exception {
        final ProjectDependencyAnalysis analysis =
            Mockito.mock(ProjectDependencyAnalysis.class);
        final Set<Artifact> unused = new HashSet<Artifact>();
        unused.add(Mockito.mock(Artifact.class));
        Mockito.doReturn(unused).when(analysis).getUsedUndeclaredArtifacts();
        final ProjectDependencyAnalyzer analyzer = this.analyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(env);
    }

    /**
     * Dependencies in runtime scope should be ignored.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testWithRuntimeScope() throws Exception {
        final ProjectDependencyAnalysis analysis =
            Mockito.mock(ProjectDependencyAnalysis.class);
        final Artifact artifact = Mockito.mock(Artifact.class);
        final Set<Artifact> unused = new HashSet<Artifact>();
        unused.add(artifact);
        Mockito.doReturn(unused).when(analysis).getUnusedDeclaredArtifacts();
        Mockito.doReturn(Artifact.SCOPE_RUNTIME).when(artifact).getScope();
        final ProjectDependencyAnalyzer analyzer = this.analyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(env);
    }

    /**
     * Create analyzer object.
     * @param analysis The analysis object
     * @return The object
     * @throws Exception If something wrong happens inside
     */
    private ProjectDependencyAnalyzer analyzer(
        final ProjectDependencyAnalysis analysis) throws Exception {
        final ProjectDependencyAnalyzer analyzer =
            Mockito.mock(ProjectDependencyAnalyzer.class);
        Mockito.doReturn(analysis).when(analyzer)
            .analyze(Mockito.any(MavenProject.class));
        return analyzer;
    }

}
