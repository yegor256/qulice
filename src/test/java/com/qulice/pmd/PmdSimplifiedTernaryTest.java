/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code SimplifiedTernary} rule.
 * @since 0.25.1
 */
final class PmdSimplifiedTernaryTest {

    @Test
    void forbidsNonSimplifiedTernaryOperators() throws Exception {
        new PmdAssert(
            "SimplifiedTernary.java",
            Matchers.is(false),
            Matchers.containsString("(SimplifiedTernary)")
        ).assertOk();
    }
}
