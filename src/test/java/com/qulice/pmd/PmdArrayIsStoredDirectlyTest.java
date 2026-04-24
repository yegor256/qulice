/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code ArrayIsStoredDirectly} rule, including the suppression
 * when a varargs/array parameter is wrapped in a method call or
 * constructor before assignment (issue #1053).
 * @since 0.25.1
 */
final class PmdArrayIsStoredDirectlyTest {

    @Test
    void allowsArrayIsStoredDirectlyWhenWrapped() throws Exception {
        new PmdAssert(
            "ArrayIsStoredDirectlyWrapped.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(ArrayIsStoredDirectly)")
            )
        ).assertOk();
    }

    @Test
    void reportsArrayIsStoredDirectlyWhenPlain() throws Exception {
        new PmdAssert(
            "ArrayIsStoredDirectlyPlain.java",
            Matchers.is(false),
            Matchers.containsString("(ArrayIsStoredDirectly)")
        ).assertOk();
    }
}
