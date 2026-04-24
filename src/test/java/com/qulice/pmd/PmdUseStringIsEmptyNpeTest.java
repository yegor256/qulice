/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Regression test for {@link UseStringIsEmptyRule}: the rule
 * must not throw a {@code NullPointerException} when it
 * encounters a pattern-matching expression.
 * @since 0.25.1
 */
final class PmdUseStringIsEmptyNpeTest {

    @Test
    void doesNotThrowNullPointerOnPatternMatching() {
        Assertions.assertDoesNotThrow(
            () -> new PmdAssert(
                "UseStringIsEmptyRuleFailsOnPatternMatching.java",
                new IsEqual<>(false),
                new StringContains("UnusedLocalVariable")
            ).assertOk()
        );
    }
}
