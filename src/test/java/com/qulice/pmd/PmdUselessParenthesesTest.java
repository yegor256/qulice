/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code UselessParentheses} rule.
 * @since 0.25.1
 */
final class PmdUselessParenthesesTest {

    @Test
    void forbidsUselessParentheses() throws Exception {
        new PmdAssert(
            "UselessParentheses.java",
            Matchers.is(false),
            Matchers.containsString("Useless parentheses")
        ).assertOk();
    }
}
