/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s tolerance of non-static,
 * non-transient fields (the {@code BeanMembersShouldSerialize} /
 * {@code NonTransientFieldInSerializableClass} rule is disabled
 * for qulice-style code).
 * @since 0.25.1
 */
final class PmdTransientFieldsTest {

    @Test
    void allowsNonTransientFields() throws Exception {
        new PmdAssert(
            "AllowNonTransientFields.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString(
                    "Found non-transient, non-static member."
                )
            )
        ).assertOk();
    }
}
