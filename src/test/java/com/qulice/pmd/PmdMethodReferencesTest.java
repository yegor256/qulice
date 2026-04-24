/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s recognition of method
 * references so that referenced private methods are not flagged
 * as {@code UnusedPrivateMethod}.
 * @since 0.25.1
 */
final class PmdMethodReferencesTest {

    @Test
    void understandsMethodReferences() throws Exception {
        new PmdAssert(
            "UnderstandsMethodReferences.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("(UnusedPrivateMethod)")
            )
        ).assertOk();
    }
}
