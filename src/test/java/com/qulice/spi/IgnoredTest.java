/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link Ignored}.
 * @since 1.0
 */
final class IgnoredTest {

    @ParameterizedTest
    @ValueSource(
        strings = {
            "/src/test/resources/com/qulice/Bad.java",
            "/src/test/resources/foo/bar/baz/Broken.java",
            "/src/site/apt/Sample.java",
            "/src/site/resources/js/script.js",
            "/src/it/violations/pom.xml",
            "/src/it/violations/src/main/java/foo/Bad.java",
            "/home/me/prj/src/test/resources/Bad.java",
            "/home/me/prj/src/it/foo/Bad.java"
        }
    )
    void ignoresFilesInIgnoredDirectories(final String path) {
        MatcherAssert.assertThat(
            String.format("%s must be ignored", path),
            new Ignored(path).yes(),
            Matchers.is(true)
        );
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "/src/main/java/com/qulice/Good.java",
            "/src/test/java/com/qulice/GoodTest.java",
            "/src/main/resources/checks.xml",
            "/src/test/groovy/com/qulice/GoodTest.groovy",
            "/src/itest/java/foo/Bad.java",
            "/src/integration/java/foo/Bad.java",
            "/src/sitemap/java/foo/Bad.java"
        }
    )
    void keepsFilesOutsideIgnoredDirectories(final String path) {
        MatcherAssert.assertThat(
            String.format("%s cannot be ignored", path),
            new Ignored(path).yes(),
            Matchers.is(false)
        );
    }

    @Test
    void ignoresDirectoryOfNestedModule() {
        MatcherAssert.assertThat(
            "Ignored directory of a nested module must be recognized",
            new Ignored("/modules/core/src/site/apt/Sample.java").yes(),
            Matchers.is(true)
        );
    }
}
