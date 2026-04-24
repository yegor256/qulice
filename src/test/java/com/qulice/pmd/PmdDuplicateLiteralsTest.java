/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s tolerance of duplicate
 * string literals when they appear inside annotations (the
 * {@code AvoidDuplicateLiterals} rule is relaxed there).
 * @since 0.25.1
 */
final class PmdDuplicateLiteralsTest {

    @Test
    void allowsDuplicateLiteralsInAnnotations() throws Exception {
        new PmdAssert(
            "AllowsDuplicateLiteralsInAnnotations.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("AvoidDuplicateLiterals")
            )
        ).assertOk();
    }
}
