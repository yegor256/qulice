/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s rejection of unicode
 * characters inside method names under the
 * {@code MethodNamingConventions} rule.
 * @since 0.25.1
 */
final class PmdMethodNamingConventionsTest {

    @Test
    void prohibitsUnicodeCharactersInMethodNames() throws Exception {
        new PmdAssert(
            "UnicodeCharactersInMethodNames.java",
            Matchers.is(false),
            Matchers.containsString("MethodNamingConventions")
        ).assertOk();
    }
}
