/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Fixture}.
 * @since 1.0
 */
final class FixtureTest {

    @Test
    void detectsJavaFileAmongTestResources() {
        MatcherAssert.assertThat(
            "Java file under src/test/resources must be a fixture",
            new Fixture("/src/test/resources/com/qulice/Bad.java").yes(),
            Matchers.is(true)
        );
    }

    @Test
    void detectsFileInSubdirectoryOfTestResources() {
        MatcherAssert.assertThat(
            "Deeply nested fixture must be recognized too",
            new Fixture("/src/test/resources/foo/bar/baz/Broken.java").yes(),
            Matchers.is(true)
        );
    }

    @Test
    void detectsFixtureByAbsolutePath() {
        MatcherAssert.assertThat(
            "Absolute path must be recognized as well",
            new Fixture("/home/me/prj/src/test/resources/Bad.java").yes(),
            Matchers.is(true)
        );
    }

    @Test
    void doesNotTouchMainSources() {
        MatcherAssert.assertThat(
            "Main source cannot be taken for a fixture",
            new Fixture("/src/main/java/com/qulice/Good.java").yes(),
            Matchers.is(false)
        );
    }

    @Test
    void doesNotTouchTestSources() {
        MatcherAssert.assertThat(
            "Test source cannot be taken for a fixture",
            new Fixture("/src/test/java/com/qulice/GoodTest.java").yes(),
            Matchers.is(false)
        );
    }

    @Test
    void doesNotTouchMainResources() {
        MatcherAssert.assertThat(
            "Main resource cannot be taken for a fixture",
            new Fixture("/src/main/resources/checks.xml").yes(),
            Matchers.is(false)
        );
    }
}
