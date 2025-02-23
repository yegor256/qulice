/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.plugin.alpha;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Simple test of the Main class.
 * @since 1.0
 */
final class MainTest {

    /**
     * Simple testing.
     */
    @Test
    void testSquare() {
        MatcherAssert.assertThat(
            "Square for 1 should be 1",
            1, Matchers.is(Main.square(1))
        );
        MatcherAssert.assertThat(
            "Square for 2 should be 4",
            4, Matchers.is(Main.square(2))
        );
    }

}
