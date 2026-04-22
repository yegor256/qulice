/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.io.File;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Relative}.
 * @since 0.24
 */
final class RelativeTest {

    @Test
    void computesRelativePathForFileInsideBasedir(@TempDir final Path dir)
        throws Exception {
        final File file = new File(dir.toFile(), "src/main/Foo.java");
        MatcherAssert.assertThat(
            "relative path under basedir must start with a forward slash",
            new Relative(dir.toFile(), file).path(),
            Matchers.equalTo("/src/main/Foo.java")
        );
    }

    @Test
    void returnsAbsolutePathWhenFileOutsideBasedir(@TempDir final Path dir)
        throws Exception {
        final File outside = new File(dir.toFile(), "sibling/Far.java");
        final File base = new File(dir.toFile(), "project");
        MatcherAssert.assertThat(
            "file outside basedir must not produce a truncated string",
            new Relative(base, outside).path(),
            Matchers.equalTo(
                outside.getAbsoluteFile().toPath().normalize()
                    .toString().replace(File.separatorChar, '/')
            )
        );
    }

    @Test
    void doesNotTruncateWhenBasedirIsStringPrefixButNotParent(
        @TempDir final Path dir
    ) throws Exception {
        final File base = new File(dir.toFile(), "foo");
        final File file = new File(dir.toFile(), "foobar/Baz.java");
        MatcherAssert.assertThat(
            "sibling with shared string prefix cannot be treated as child",
            new Relative(base, file).path(),
            Matchers.not(Matchers.startsWith("bar/"))
        );
    }

    @Test
    void doesNotThrowWhenFilePathShorterThanBasedir(@TempDir final Path dir)
        throws Exception {
        final File deep = new File(dir.toFile(), "a/very/deep/basedir");
        final File shallow = new File("x.java");
        MatcherAssert.assertThat(
            "shallow file must not cause a substring-index exception",
            new Relative(deep, shallow).path(),
            Matchers.notNullValue()
        );
    }
}
