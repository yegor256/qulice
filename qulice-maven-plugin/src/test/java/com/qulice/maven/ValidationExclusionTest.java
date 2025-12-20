/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.checkstyle.CheckstyleValidator;
import com.qulice.pmd.PmdValidator;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link DefaultMavenEnvironment} class methods that
 * exclude files from validation.
 * @since 0.19
 */
final class ValidationExclusionTest {
    /**
     * Temporary directory for the project subfolder to be excluded.
     */
    private static final String TEMP_SUB = "excl";

    /**
     * Java files extension.
     */
    private static final String JAVA_EXT = ".java";

    /**
     * DefaultMavenEnvironment can exclude a path from PMD validation.
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludePathFromPmdValidation(@TempDir final Path dir) throws Exception {
        final var env = new DefaultMavenEnvironment();
        final var subdir = Files.createTempDirectory(dir, ValidationExclusionTest.TEMP_SUB);
        final var project = new MavenProject();
        project.setFile(subdir.toFile());
        env.setProject(project);
        Assertions.assertNotNull(
            project.getBasedir(),
            "We expect basedir to be set"
        );
        final var file = ValidationExclusionTest.java(
            subdir, "com/qulice/maven/ValidationExclusion/PmdExample.txt"
        );
        env.setExcludes(
            Collections.singletonList(
                String.format("pmd:/%s/.*", subdir.getFileName())
            )
        );
        Assertions.assertTrue(
            new PmdValidator(env).getNonExcludedFiles(
                Collections.singletonList(file)
            ).isEmpty(),
            "We expect no files to be returned for PMD validation"
        );
    }

    /**
     * DefaultMavenEnvironment can exclude a path from Checkstyle validation.
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludePathFromCheckstyleValidation(@TempDir final Path dir) throws Exception {
        final var env = new DefaultMavenEnvironment();
        final var subdir = Files.createTempDirectory(dir, ValidationExclusionTest.TEMP_SUB);
        final var build = new Build();
        build.setOutputDirectory(dir.toString());
        final var project = new MavenProject();
        project.setFile(subdir.toFile());
        project.setBuild(build);
        env.setProject(project);
        Assertions.assertNotNull(
            project.getBasedir(),
            "We expect basedir to be set"
        );
        Assertions.assertNotNull(
            env.tempdir(),
            "We expect tempdir to be set"
        );
        final var file = ValidationExclusionTest.java(
            subdir, "com/qulice/maven/ValidationExclusion/CheckstyleExample.txt"
        );
        env.setExcludes(
            Collections.singletonList(
                String.format("checkstyle:/%s/.*", subdir.getFileName())
            )
        );
        final var validator = new CheckstyleValidator(env);
        final var files = validator.getNonExcludedFiles(Collections.singletonList(file));
        Assertions.assertTrue(
            files.isEmpty(),
            "We expect no files to be returned for Checkstyle validation"
        );
    }

    /**
     * DefaultMavenEnvironment can exclude a path from entire validation.
     * @param dir Temporary directory
     * @throws Exception If something wrong happens inside
     * @todo #1457:90min Add global exclusion support to qulice.
     *  Currently, DefaultMavenEnvironment and validators support only
     *  tool-specific exclusions. Once global exclusion support is added,
     *  the {@link #excludePathFromEntireValidation}
     *  should be enabled and verified to work as expected.
     *  The original task comes from:
     *  <a href="https://github.com/yegor256/qulice/issues/1457">#1457"</a>
     */
    @Test
    void excludePathFromEntireValidation(@TempDir final Path dir) throws Exception {
        final var env = new DefaultMavenEnvironment();
        final var subdir = Files.createTempDirectory(dir, ValidationExclusionTest.TEMP_SUB);
        final var build = new Build();
        build.setOutputDirectory(dir.toString());
        final var project = new MavenProject();
        project.setFile(subdir.toFile());
        project.setBuild(build);
        env.setProject(project);
        env.setExcludes(
            Collections.singletonList(
                String.format("*:/%s/.*", subdir.getFileName())
            )
        );
        final List<File> included = Arrays.asList(
            ValidationExclusionTest.java(
                subdir, "com/qulice/maven/ValidationExclusion/CheckstyleExample.txt"
            ),
            ValidationExclusionTest.java(
                subdir, "com/qulice/maven/ValidationExclusion/PmdExample.txt"
            )
        );
        final var pmd = new PmdValidator(env).getNonExcludedFiles(included).size();
        final var checkstyle = new CheckstyleValidator(env).getNonExcludedFiles(included).size();
        final var total = pmd + checkstyle;
        Assertions.assertEquals(
            0,
            total,
            String.format(
                "We expect no files to be returned for entire validation, but found %d files",
                total
            )
        );
    }

    /**
     * Create a temporary Java file from resource.
     * @param dir Directory to create the file in
     * @param resource Resource to read the content from
     * @return Created file with content from resource
     * @throws Exception If something goes wrong
     */
    private static File java(final Path dir, final String resource) throws Exception {
        final var file = File.createTempFile(
            "Test", ValidationExclusionTest.JAVA_EXT, dir.toFile()
        );
        FileUtils.writeStringToFile(
            file,
            new TextOf(
                new ResourceOf(resource)
            ).asString(),
            StandardCharsets.UTF_8
        );
        return file;
    }
}
