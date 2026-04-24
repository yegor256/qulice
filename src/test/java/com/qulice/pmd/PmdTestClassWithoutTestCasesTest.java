/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s recognition of the
 * {@code io.github.artsok.RepeatedIfExceptionsTest} annotation
 * as a test method, so the class is not flagged under
 * {@code TestClassWithoutTestCases}.
 * @since 0.25.1
 */
final class PmdTestClassWithoutTestCasesTest {

    @Test
    void recognisesArtsokRepeatedIfExceptionsTest() throws Exception {
        new PmdAssert(
            "RepeatedIfExceptionsTest.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("TestClassWithoutTestCases")
            )
        ).assertOk();
    }
}
