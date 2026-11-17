/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.util.Collections;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link SnapshotsValidator} class.
 *
 * @since 1.0
 */
final class SnapshotsValidatorTest {

    /**
     * SnapshotsValidator passes a stable project that declares a build
     * plugin without a version. An unversioned plugin declaration is not
     * a SNAPSHOT and must not crash validation with a
     * {@link NullPointerException} (see
     * <a href="https://github.com/yegor256/qulice/issues/1869">
     * issue #1869</a>).
     */
    @Test
    void toleratesBuildPluginWithoutVersion() {
        final Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-javadoc-plugin");
        Assertions.assertDoesNotThrow(
            () -> new SnapshotsValidator().validate(
                SnapshotsValidatorTest.envWith(
                    SnapshotsValidatorTest.releaseProjectWith(plugin)
                )
            )
        );
    }

    /**
     * SnapshotsValidator fails a stable project that declares a build
     * plugin in SNAPSHOT state.
     */
    @Test
    void catchesSnapshotBuildPlugin() {
        final Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-javadoc-plugin");
        plugin.setVersion("3.2.0-SNAPSHOT");
        Assertions.assertThrows(
            ValidationException.class,
            () -> new SnapshotsValidator().validate(
                SnapshotsValidatorTest.envWith(
                    SnapshotsValidatorTest.releaseProjectWith(plugin)
                )
            )
        );
    }

    private static MavenProject releaseProjectWith(final Plugin plugin) {
        final Build build = new Build();
        build.addPlugin(plugin);
        final MavenProject project = new MavenProject();
        project.setGroupId("com.example");
        project.setArtifactId("sample");
        project.setVersion("1.0");
        project.setBuild(build);
        return project;
    }

    private static MavenEnvironment envWith(final MavenProject project)
        throws Exception {
        return new MavenEnvironment.Wrap(
            new Environment.Mock(),
            new FakeMavenEnvironment(
                project,
                new FakeContext(new DefaultPlexusContainer()),
                Collections.emptyList()
            )
        );
    }
}
