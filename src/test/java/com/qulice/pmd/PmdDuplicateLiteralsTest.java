/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s tolerance of repeated string
 * literals, since the {@code AvoidDuplicateLiterals} rule is not part
 * of the ruleset.
 * @since 0.25.1
 */
final class PmdDuplicateLiteralsTest {

    @Test
    void toleratesDuplicateLiterals() throws Exception {
        new PmdAssert(
            "ToleratesDuplicateLiterals.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("AvoidDuplicateLiterals")
            )
        ).assertOk();
    }
}
