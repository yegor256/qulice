/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.jcabi.log.Logger;
import com.qulice.spi.ValidationException;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;
import org.cactoos.text.Joined;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.ContextException;

/**
 * Validator of dependencies.
 *
 * @since 0.3
 * @checkstyle ReturnCountCheck (100 line)
 */
final class DependenciesValidator implements MavenValidator {

    /**
     * Separator between lines.
     */
    private static final String SEP = "\n\t";

    @Override
    @SuppressWarnings("PMD.OnlyOneReturn")
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        if (!env.outdir().exists() || "pom".equals(env.project().getPackaging())) {
            Logger.info(this, "No dependency analysis in this project");
            return;
        }
        final Collection<String> excludes = env.excludes("dependencies");
        if (excludes.contains(".*")) {
            Logger.info(this, "Dependency analysis suppressed in the project via pom.xml");
            return;
        }
        final Collection<String> unused = Collections2.filter(
            DependenciesValidator.unused(env),
            Predicates.not(new DependenciesValidator.ExcludePredicate(excludes))
        );
        if (!unused.isEmpty()) {
            Logger.warn(
                this,
                "Unused declared dependencies found:%s%s",
                DependenciesValidator.SEP,
                new Joined(DependenciesValidator.SEP, unused).toString()
            );
        }
        final Collection<String> used = Collections2.filter(
            DependenciesValidator.used(env),
            Predicates.not(new DependenciesValidator.ExcludePredicate(excludes))
        );
        if (!used.isEmpty()) {
            Logger.warn(
                this,
                "Used undeclared dependencies found:%s%s",
                DependenciesValidator.SEP,
                new Joined(DependenciesValidator.SEP, used)
            );
        }
        if (!used.isEmpty() || !unused.isEmpty()) {
            Logger.info(
                this,
                "You can suppress this message by <exclude>dependencies:...</exclude> in pom.xml, where <...> is what the dependency name starts with (not a regular expression!)"
            );
        }
        final int failures = used.size() + unused.size();
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
    private static ProjectDependencyAnalysis analyze(
        final MavenEnvironment env) {
        try {
            return ((ProjectDependencyAnalyzer)
                ((PlexusContainer)
                    env.context().get(PlexusConstants.PLEXUS_KEY)
                ).lookup(ProjectDependencyAnalyzer.class.getName(), "default")
            ).analyze(env.project());
        } catch (final ContextException | ComponentLookupException
            | ProjectDependencyAnalyzerException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Find unused artifacts.
     * @param env Environment
     * @return Collection of unused artifacts
     */
    private static Collection<String> used(final MavenEnvironment env) {
        final ProjectDependencyAnalysis analysis =
            DependenciesValidator.analyze(env);
        final Collection<String> used = new LinkedList<>();
        for (final Object artifact : analysis.getUsedUndeclaredArtifacts()) {
            used.add(artifact.toString());
        }
        return used;
    }

    /**
     * Find unused artifacts.
     * @param env Environment
     * @return Collection of unused artifacts
     */
    private static Collection<String> unused(final MavenEnvironment env) {
        final ProjectDependencyAnalysis analysis =
            DependenciesValidator.analyze(env);
        final Collection<String> unused = new LinkedList<>();
        for (final Object obj : analysis.getUnusedDeclaredArtifacts()) {
            final Artifact artifact = (Artifact) obj;
            if (!Artifact.SCOPE_COMPILE.equals(artifact.getScope())) {
                continue;
            }
            unused.add(artifact.toString());
        }
        return unused;
    }

    /**
     * Predicate for excluded dependencies.
     *
     * @since 0.1
     */
    private static class ExcludePredicate implements Predicate<String> {

        /**
         * List of excludes.
         */
        private final Collection<String> excludes;

        /**
         * Constructor.
         * @param excludes List of excludes.
         */
        ExcludePredicate(final Collection<String> excludes) {
            this.excludes = excludes;
        }

        @Override
        public boolean apply(final String name) {
            boolean ignore = false;
            for (final String exclude : this.excludes) {
                if (name.startsWith(exclude)) {
                    ignore = true;
                    break;
                }
            }
            return ignore;
        }
    }
}
