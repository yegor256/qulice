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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.sourceforge.pmd.util.datasource.DataSource;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link DefaultMavenEnvironment} class methods that
 * exclude files from validation.
 * @since 0.19
 */
final class ValidationExclusionTest {
    /**
     * Temporary directory for the project source folder.
     */
    private static final String TEMP_DIR = "src";

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
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludePathFromPmdValidation() throws Exception {
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        final MavenProject project = Mockito.mock(MavenProject.class);
        final Path dir = Files.createTempDirectory(ValidationExclusionTest.TEMP_DIR);
        final Path subdir = Files.createTempDirectory(dir, ValidationExclusionTest.TEMP_SUB);
        final File file = File.createTempFile(
            "PmdExample", ValidationExclusionTest.JAVA_EXT,
            subdir.toFile()
        );
        Mockito.when(project.getBasedir())
            .thenReturn(
                dir.toFile()
            );
        env.setProject(project);
        Assertions.assertNotNull(project.getBasedir());
        final String source = new TextOf(
            new ResourceOf("com/qulice/maven/ValidationExclusion/PmdExample.txt")
        ).asString();
        FileUtils.forceDeleteOnExit(file);
        FileUtils.writeStringToFile(
            file,
            source,
            StandardCharsets.UTF_8
        );
        env.setExcludes(
            Collections.singletonList(
                String.format("pmd:/%s/.*", subdir.getFileName())
            )
        );
        final PmdValidator validator = new PmdValidator(env);
        final Collection<DataSource> files = validator.getNonExcludedFiles(
            Collections.singletonList(file)
        );
        Assertions.assertTrue(files.isEmpty());
    }

    /**
     * DefaultMavenEnvironment can exclude a path from Checkstyle validation.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void excludePathFromCheckstyleValidation() throws Exception {
        final DefaultMavenEnvironment env = new DefaultMavenEnvironment();
        final MavenProject project = Mockito.mock(MavenProject.class);
        final Path dir = Files.createTempDirectory(ValidationExclusionTest.TEMP_DIR);
        final Path subdir = Files.createTempDirectory(dir, ValidationExclusionTest.TEMP_SUB);
        final File file = File.createTempFile(
            "CheckstyleExample", ValidationExclusionTest.JAVA_EXT,
            subdir.toFile()
        );
        env.setProject(project);
        Mockito.when(project.getBasedir()).thenReturn(dir.toFile());
        final Build build = new Build();
        build.setOutputDirectory(dir.toString());
        Mockito.when(project.getBuild()).thenReturn(build);
        Assertions.assertNotNull(project.getBasedir());
        Assertions.assertNotNull(env.tempdir());
        final String source = new TextOf(
            new ResourceOf("com/qulice/maven/ValidationExclusion/CheckstyleExample.txt")
        ).asString();
        FileUtils.forceDeleteOnExit(file);
        FileUtils.writeStringToFile(
            file,
            source,
            StandardCharsets.UTF_8
        );
        env.setExcludes(
            Collections.singletonList(
                String.format("checkstyle:/%s/.*", subdir.getFileName())
            )
        );
        final CheckstyleValidator validator = new CheckstyleValidator(env);
        final List<File> files = validator.getNonExcludedFiles(Collections.singletonList(file));
        Assertions.assertTrue(files.isEmpty());
    }
}
