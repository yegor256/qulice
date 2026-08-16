/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Environment;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Release}.
 * @since 1.0
 */
final class ReleaseTest {

    /**
     * Name of the Maven property holding the release level of main sources.
     */
    private static final String RELEASE = "maven.compiler.release";

    /**
     * Name of the Maven property holding the release level of test sources.
     */
    private static final String TEST_RELEASE = "maven.compiler.testRelease";

    @Test
    void addsNothingWhenNoLevelIsPinned() {
        MatcherAssert.assertThat(
            "an unpinned project must keep the host default level",
            new Release(new Environment.Mock(), "main").flags(),
            Matchers.empty()
        );
    }

    @Test
    void forwardsTheReleaseOfMainSources() {
        MatcherAssert.assertThat(
            "the release of the project must reach javac",
            new Release(
                new Environment.Mock().withParam(ReleaseTest.RELEASE, "8"),
                "main"
            ).flags(),
            Matchers.contains("--release", "8")
        );
    }

    @Test
    void forwardsSourceAndTargetWhenNoReleaseIsPinned() {
        MatcherAssert.assertThat(
            "a project pinning only -source must keep its API surface",
            new Release(
                new Environment.Mock()
                    .withParam("maven.compiler.source", "1.8"),
                "main"
            ).flags(),
            Matchers.contains("-source", "8", "-target", "8")
        );
    }

    @Test
    void forwardsTheTestReleaseToTheTestBatch() {
        MatcherAssert.assertThat(
            "tests compiled higher than main code must say so",
            new Release(
                new Environment.Mock()
                    .withParam(ReleaseTest.RELEASE, "8")
                    .withParam(ReleaseTest.TEST_RELEASE, "17"),
                "test"
            ).flags(),
            Matchers.contains("--release", "17")
        );
    }

    @Test
    void prefersTheTestSourceOverTheReleaseOfMainSources() {
        MatcherAssert.assertThat(
            "a test-specific -source must outrank the main --release",
            new Release(
                new Environment.Mock()
                    .withParam(ReleaseTest.RELEASE, "8")
                    .withParam("maven.compiler.testSource", "17"),
                "test"
            ).flags(),
            Matchers.contains("-source", "17", "-target", "17")
        );
    }

    @Test
    void fallsBackToMainLevelWhenTestsPinNothing() {
        MatcherAssert.assertThat(
            "tests of a project pinning one level must compile at it",
            new Release(
                new Environment.Mock().withParam(ReleaseTest.RELEASE, "11"),
                "test"
            ).flags(),
            Matchers.contains("--release", "11")
        );
    }

    @Test
    void keepsMainSourcesAwayFromTheTestLevel() {
        MatcherAssert.assertThat(
            "the level of the tests must not reach the main batch",
            new Release(
                new Environment.Mock()
                    .withParam(ReleaseTest.RELEASE, "8")
                    .withParam(ReleaseTest.TEST_RELEASE, "17"),
                "main"
            ).flags(),
            Matchers.contains("--release", "8")
        );
    }

    @Test
    void forwardsTheTestTargetAlongsideTheTestSource() {
        MatcherAssert.assertThat(
            "a test-specific -target must be forwarded too",
            new Release(
                new Environment.Mock()
                    .withParam("maven.compiler.testSource", "11")
                    .withParam("maven.compiler.testTarget", "17"),
                "test"
            ).flags(),
            Matchers.contains("-source", "11", "-target", "17")
        );
    }
}
