/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.util.Collection;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;

/**
 * A test fake {@link ProjectDependencyAnalyzer} that returns a
 * pre-built {@link ProjectDependencyAnalysis} regardless of the
 * arguments it is given.
 *
 * <p>Lets {@code DependenciesValidator} tests stage exactly the
 * "used", "unused declared" and "used undeclared" artifact sets they
 * want without having to drive a real Maven dependency analysis.</p>
 *
 * @since 0.27.0
 */
final class FakeProjectDependencyAnalyzer implements ProjectDependencyAnalyzer {

    /**
     * ProjectDependencyAnalysis.
     */
    private final ProjectDependencyAnalysis analysis;

    FakeProjectDependencyAnalyzer(final ProjectDependencyAnalysis alysis) {
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
