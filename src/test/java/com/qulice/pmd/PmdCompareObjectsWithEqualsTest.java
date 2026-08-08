/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code CompareObjectsWithEquals} rule.
 * @since 1.0
 */
final class PmdCompareObjectsWithEqualsTest {

    @Test
    void allowsComparingFuturesByReference() throws Exception {
        new PmdAssert(
            "CompareFuturesWithEquals.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(CompareObjectsWithEquals)")
            )
        ).assertOk();
    }

    @Test
    void forbidsComparingPlainObjectsByReference() throws Exception {
        new PmdAssert(
            "CompareObjectsByReference.java",
            Matchers.is(false),
            Matchers.containsString("(CompareObjectsWithEquals)")
        ).assertOk();
    }
}
