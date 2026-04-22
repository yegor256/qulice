/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Joiner;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link DependenciesValidator} class.
 *
 * @since 0.3
 */
final class DependenciesValidatorTest {
    /**
     * Plexus role.
     */
    private static final String ROLE =
        ProjectDependencyAnalyzer.class.getName();

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
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void passesIfNoDependencyProblemsFound() throws Exception {
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis();
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(env);
    }

    /**
     * DependencyValidator can catch dependency problems.
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void catchesDependencyProblemsAndThrowsException() throws Exception {
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("group");
        artifact.setArtifactId("artifact");
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("2.3.4");
        artifact.setType(DependenciesValidatorTest.TYPE);
        final Set<Artifact> unused = new HashSet<>();
        unused.add(artifact);
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis(
                Collections.emptySet(), unused, Collections.emptySet()
            );
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
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
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void ignoresRuntimeScope() throws Exception {
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("group");
        artifact.setArtifactId("artifact");
        artifact.setScope("runtime");
        artifact.setVersion("2.3.4");
        artifact.setType(DependenciesValidatorTest.TYPE);
        final Set<Artifact> unused = new HashSet<>();
        unused.add(artifact);
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis(
                Collections.emptySet(), Collections.emptySet(), unused
            );
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
        final MavenEnvironment env = new MavenEnvironmentMocker().inPlexus(
            DependenciesValidatorTest.ROLE,
            DependenciesValidatorTest.HINT,
            analyzer
        ).mock();
        new DependenciesValidator().validate(env);
    }

    /**
     * DependencyValidator can exclude used undeclared dependencies.
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludesUsedUndeclaredDependencies() throws Exception {
        final Set<Artifact> used = new HashSet<>();
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("group");
        artifact.setArtifactId("artifact");
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("2.3.4");
        artifact.setType(DependenciesValidatorTest.TYPE);
        used.add(artifact);
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis(
                Collections.emptySet(), used, Collections.emptySet()
            );
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
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
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludesUnusedDeclaredDependencies() throws Exception {
        final Set<Artifact> unused = new HashSet<>();
        final ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("othergroup");
        artifact.setArtifactId("otherartifact");
        artifact.setScope(DependenciesValidatorTest.SCOPE);
        artifact.setVersion("1.2.3");
        artifact.setType(DependenciesValidatorTest.TYPE);
        unused.add(artifact);
        final ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis(
                Collections.emptySet(), Collections.emptySet(), unused
            );
        final ProjectDependencyAnalyzer analyzer =
            new FakeProjectDependencyAnalyzer(analysis);
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
     * FakeProjectDependencyAnalyzer.
     *
     * A mock to ProjectDependencyAnalyzer.
     *
     * @since 0.24.1
     */
    private static final class FakeProjectDependencyAnalyzer
        implements ProjectDependencyAnalyzer {
        /**
         * ProjectDependencyAnalysis.
         */
        private final ProjectDependencyAnalysis analysis;

        FakeProjectDependencyAnalyzer(
            final ProjectDependencyAnalysis alysis
        ) {
            this.analysis = alysis;
        }

        @Override
        public ProjectDependencyAnalysis analyze(
            final MavenProject project,
            final Collection<String> collection
        ) throws ProjectDependencyAnalyzerException {
            return this.analysis;
        }
    }
}
