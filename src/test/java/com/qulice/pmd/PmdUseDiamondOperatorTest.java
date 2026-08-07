/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code UseDiamondOperator} rule.
 * @since 1.0
 */
final class PmdUseDiamondOperatorTest {

    @Test
    void forbidsExplicitTypeArgumentsThatDiamondCanReplace() throws Exception {
        new PmdAssert(
            "UseDiamondOperator.java",
            Matchers.is(false),
            Matchers.containsString("(UseDiamondOperator)")
        ).assertOk();
    }

    @Test
    void allowsExplicitTypeArgumentsRequiredForInference() throws Exception {
        new PmdAssert(
            "UseDiamondOperatorInference.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(UseDiamondOperator)")
            )
        ).assertOk();
    }

    @Test
    void allowsExplicitTypeArgumentsOnAnonymousClass() throws Exception {
        new PmdAssert(
            "UseDiamondOperatorAnonymous.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(UseDiamondOperator)")
            )
        ).assertOk();
    }
}
