/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.collect.ImmutableList;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link DefaultMavenEnvironment} class.
 * @since 0.8
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class DefaultMavenEnvironmentTest {

    /**
     * DefaultMavenEnvironment can produce list of excludes.
     */
    @Test
    void excludeAllFiles() {
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        env.setExcludes(Collections.singletonList("codenarc:**/*.groovy"));
        MatcherAssert.assertThat(
            "Excludes should be returned",
            env.excludes("codenarc"),
            Matchers.contains("**/*.groovy")
        );
    }

    /**
     * DefaultMavenEnvironment can produce list of excludes from empty source.
     */
    @Test
    void emptyExclude() {
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        env.setExcludes(Collections.<String>emptyList());
        MatcherAssert.assertThat(
            "Empty list should be returned",
            env.excludes("codenarc").iterator().hasNext(),
            Matchers.is(false)
        );
    }

    /**
     * DefaultMavenEnvironment can produce list of excludes without excludes.
     */
    @Test
    void noExclude() {
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        MatcherAssert.assertThat(
            "Excludes should be empty list by default",
            env.excludes("codenarc").iterator().hasNext(),
            Matchers.is(false)
        );
    }

    /**
     * DefaultMavenEnvironment can produce list of excludes.
     */
    @Test
    void excludeSomeFiles() {
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        env.setExcludes(
            ImmutableList.<String>builder()
                .add("codenarc:**/src/ex1/Main.groovy")
                .add("codenarc:**/src/ex2/Main2.groovy")
                .build()
        );
        MatcherAssert.assertThat(
            "Excludes should be returned as list",
            env.excludes("codenarc"),
            Matchers.containsInAnyOrder(
                "**/src/ex1/Main.groovy",
                "**/src/ex2/Main2.groovy"
            )
        );
    }

    /**
     * DefaultMavenEnvironment can work with whitespaces in classpath.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void passPathsWithWhitespaces()  throws Exception {
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        final MavenProjectStub project = new MavenProjectStub();
        project.setRuntimeClasspathElements(
            Collections.singletonList("/Users/Carlos Miranda/git")
        );
        project.setDependencyArtifacts(Collections.emptySet());
        env.setProject(project);
        MatcherAssert.assertThat(
            "ClassPath should be returned",
            env.classloader(),
            Matchers.notNullValue()
        );
    }

    /**
     * DefaultMavenEnvironment can produce empty collection when no matches
     * with checker.
     */
    @Test
    void producesEmptyExcludesWhenNoMatches() {
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        env.setExcludes(
            ImmutableList.of(
                "checkstyle:**/src/ex1/Main.groovy",
                "pmd:**/src/ex2/Main2.groovy"
            )
        );
        MatcherAssert.assertThat(
            "Exclude dependencies should be empty",
            env.excludes("dependencies"),
            Matchers.empty()
        );
    }

    /**
     * Default source files encoding should be UFT-8.
     */
    @Test
    void defaultEncodingIsUtf() {
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        MatcherAssert.assertThat(
            "Default encoding should be UTF-8",
            env.encoding(),
            Matchers.is(StandardCharsets.UTF_8)
        );
    }
}
