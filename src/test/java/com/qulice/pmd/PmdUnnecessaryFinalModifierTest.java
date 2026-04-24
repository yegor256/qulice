/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code UnnecessaryFinalModifier} rule.
 * @since 0.25.1
 */
final class PmdUnnecessaryFinalModifierTest {

    @Test
    void forbidsUnnecessaryFinalModifier() throws Exception {
        new PmdAssert(
            "UnnecessaryFinalModifier.java",
            Matchers.is(false),
            Matchers.containsString("Unnecessary modifier 'final'")
        ).assertOk();
    }
}
