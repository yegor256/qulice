/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.io.File;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Environment}.
 * @since 0.3
 */
final class EnvironmentTest {

    /**
     * Environment interface can be mocked/instantiated with Mocker.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void canBeInstantiatedWithMocker() throws Exception {
        final Environment env = new Environment.Mock();
        MatcherAssert.assertThat(
            "Basedir should exist", env.basedir().exists(), Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Tempdir should exist", env.tempdir().exists(), Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Outdir should exist", env.outdir().exists(), Matchers.is(true)
        );
    }

    /**
     * EnvironmentMocker can create file.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void writesFileContentToTheDesignatedLocation() throws Exception {
        final String name = "src/main/java/Main.java";
        final String content = "class Main {}";
        final Environment env = new Environment.Mock()
            .withFile(name, content);
        final File file = new File(env.basedir(), name);
        MatcherAssert.assertThat(
            "File should be created in basedir from string value",
            file.exists(), Matchers.is(true)
        );
    }

    /**
     * EnvironmentMocker can write bytearray too.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void writesByteArrayToTheDesignatedLocation() throws Exception {
        final String name = "src/main/java/Foo.java";
        final byte[] bytes = "class Foo {}".getBytes();
        final Environment env = new Environment.Mock()
            .withFile(name, bytes);
        final File file = new File(env.basedir(), name);
        MatcherAssert.assertThat(
            "File should be created in basedir from bytes",
            file.exists(), Matchers.is(true)
        );
    }

    /**
     * EnvironmentMocker can set classpath for the mock.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void setsClasspathOnTheMock() throws Exception {
        final Environment env = new Environment.Mock();
        MatcherAssert.assertThat(
            "Classpath should be not empty",
            env.classpath().size(),
            Matchers.greaterThan(0)
        );
    }

    /**
     * Environment.files() should skip binary files so validators never see
     * them (see <a href="https://github.com/yegor256/qulice/issues/1264">
     * issue #1264</a>).
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void skipsBinaryFilesWhenListing() throws Exception {
        final String image = "src/main/resources/pixel.png";
        final String source = "src/main/java/Foo.java";
        final Environment env = new Environment.Mock()
            .withFile(source, "class Foo {}\n").withFile(
                image,
                new byte[] {
                    (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
                    0x00, 0x00, 0x00, 0x0d, 0x49, 0x48, 0x44, 0x52,
                }
            );
        MatcherAssert.assertThat(
            "Binary files cannot leak into the list of files to validate",
            env.files("*.*"),
            Matchers.allOf(
                Matchers.hasItem(new File(env.basedir(), source)),
                Matchers.not(Matchers.hasItem(new File(env.basedir(), image)))
            )
        );
    }

    /**
     * EnvironmentMocker can mock params.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void configuresParametersInMock() throws Exception {
        final String name = "alpha";
        final String value = "some complex value";
        final Environment env = new Environment.Mock()
            .withParam(name, value);
        MatcherAssert.assertThat(
            "Environment variable should be set",
            env.param(name, ""), Matchers.equalTo(value)
        );
    }
}
