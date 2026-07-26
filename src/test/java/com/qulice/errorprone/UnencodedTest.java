/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import java.io.File;
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Unencoded}.
 * @since 1.0
 */
final class UnencodedTest {

    @Test
    void decodesSpaceInDirectoryName(@TempDir final File dir) throws Exception {
        final File jar = new File(new File(dir, "My Project"), "foo.jar");
        MatcherAssert.assertThat(
            "space in a directory name cannot survive as an escape",
            new Unencoded(jar.toURI().toURL()).path().orElse(""),
            Matchers.equalTo(jar.getAbsolutePath())
        );
    }

    @Test
    void dontResolveRemoteUrls() throws Exception {
        MatcherAssert.assertThat(
            "remote URL cannot resolve to a local path",
            new Unencoded(
                URI.create("http://example.com/foo.jar").toURL()
            ).path().isPresent(),
            Matchers.is(false)
        );
    }
}
