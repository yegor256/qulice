/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link DefaultMavenEnvironment} class.
 * @since 0.8
 */
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
        MatcherAssert.assertThat(
            "Excludes should be empty list by default",
            new DefaultMavenEnvironment().excludes("codenarc")
                .iterator().hasNext(),
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
    void passPathsWithWhitespaces() throws Exception {
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
     * DefaultMavenEnvironment.files() should silently drop binary files so
     * that validators never try to read them as text
     * (see <a href="https://github.com/yegor256/qulice/issues/1264">
     * issue #1264</a>).
     * @param basedir Temporary base directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void skipsBinaryFilesWhenListing(@TempDir final Path basedir)
        throws Exception {
        final Path src = basedir.resolve("src/main/java");
        Files.createDirectories(src);
        final Path source = src.resolve("Foo.java");
        Files.writeString(
            source,
            "class Foo {}".concat(String.valueOf('\n')),
            StandardCharsets.UTF_8
        );
        final Path image = basedir.resolve("src/main/resources/pixel.png");
        Files.createDirectories(image.getParent());
        Files.write(
            image,
            new byte[] {
                (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
                0x00, 0x00, 0x00, 0x0d, 0x49, 0x48, 0x44, 0x52,
            }
        );
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        env.setProject(
            new MavenProjectStub() {
                @Override
                public File getBasedir() {
                    return basedir.toFile();
                }
            }
        );
        MatcherAssert.assertThat(
            "Binary files cannot leak into the list of files to validate",
            env.files("*.*"),
            Matchers.allOf(
                Matchers.hasItem(source.toFile()),
                Matchers.not(Matchers.hasItem(image.toFile()))
            )
        );
    }

    /**
     * Default source files encoding should be UFT-8.
     */
    @Test
    void defaultEncodingIsUtf() {
        MatcherAssert.assertThat(
            "Default encoding should be UTF-8",
            new DefaultMavenEnvironment().encoding(),
            Matchers.is(StandardCharsets.UTF_8)
        );
    }
}
