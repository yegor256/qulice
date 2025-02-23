/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Joiner;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link DependenciesValidator} class.
 * @since 0.3
 */
final class DependenciesValidatorTest {

    /**
     * Plexus role.
     */
    private static final String ROLE = ProjectDependencyAnalyzer.class.getName();

    /**
     * Plexus hint.
     */
    private static final String HINT = "default";

    /**
     * Compile scope.
     */
    private static final String SCOPE = "compile";

    /**
     * Jar type.
     */
    private static final String TYPE = "jar";

    /**
     * DependencyValidator can pass on when no violations are found.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void passesIfNoDependencyProblemsFound() throws Exception {
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
    @Test
    void catchesDependencyProblemsAndThrowsException() throws Exception {
        final ProjectDependencyAnalysis analysis =
            Mockito.mock(ProjectDependencyAnalysis.class);
        final Set<Artifact> unused = new HashSet<>();
        unused.add(Mockito.mock(Artifact.class));
        Mockito.doReturn(unused).when(analysis).getUsedUndeclaredArtifacts();
        final ProjectDependencyAnalyzer analyzer = this.analyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        Assertions.assertThrows(
            ValidationException.class,
            () -> new DependenciesValidator().validate(env)
        );
    }

    /**
     * DependencyValidator can ignore runtime scope dependencies.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void ignoresRuntimeScope() throws Exception {
        final ProjectDependencyAnalysis analysis =
            Mockito.mock(ProjectDependencyAnalysis.class);
        final Artifact artifact = Mockito.mock(Artifact.class);
        final Set<Artifact> unused = new HashSet<>();
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
     * DependencyValidator can exclude used undeclared dependencies.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludesUsedUndeclaredDependencies() throws Exception {
        final ProjectDependencyAnalysis analysis =
            Mockito.mock(ProjectDependencyAnalysis.class);
        final Set<Artifact> used = new HashSet<>();
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("group");
        artifact.setArtifactId("artifact");
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("2.3.4");
        artifact.setType(DependenciesValidatorTest.TYPE);
        used.add(artifact);
        Mockito.doReturn(used).when(analysis).getUsedUndeclaredArtifacts();
        final ProjectDependencyAnalyzer analyzer = this.analyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(
            new MavenEnvironment.Wrap(
                new Environment.Mock().withExcludes(
                    Joiner.on(':').join(
                        artifact.getGroupId(), artifact.getArtifactId()
                    )
                ), env
            )
        );
    }

    /**
     * DependencyValidator can exclude unused declared dependencies.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludesUnusedDeclaredDependencies() throws Exception {
        final ProjectDependencyAnalysis analysis =
            Mockito.mock(ProjectDependencyAnalysis.class);
        final Set<Artifact> unused = new HashSet<>();
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("othergroup");
        artifact.setArtifactId("otherartifact");
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("1.2.3");
        artifact.setType(DependenciesValidatorTest.TYPE);
        unused.add(artifact);
        Mockito.doReturn(unused).when(analysis).getUnusedDeclaredArtifacts();
        final ProjectDependencyAnalyzer analyzer = this.analyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(
            new MavenEnvironment.Wrap(
                new Environment.Mock().withExcludes(
                    Joiner.on(':').join(
                        artifact.getGroupId(), artifact.getArtifactId()
                    )
                ), env
            )
        );
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
