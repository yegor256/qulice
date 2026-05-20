/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code TooManyMethods} rule, which is disabled for unit test
 * classes so that splitting asserts into separate {@code @Test}
 * methods (required by {@code UnitTestContainsTooManyAsserts})
 * does not push the class past the threshold.
 * Regression test for https://github.com/yegor256/qulice/issues/1605
 * @since 1.0
 */
final class PmdTooManyMethodsTest {

    @Test
    void allowsManyMethodsInTestClass() throws Exception {
        new PmdAssert(
            "ManyMethodsTest.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("TooManyMethods")
            )
        ).assertOk();
    }

    @Test
    void allowsManyMethodsInIntegrationTestClass() throws Exception {
        new PmdAssert(
            "ManyMethodsIT.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("TooManyMethods")
            )
        ).assertOk();
    }

    @Test
    void reportsTooManyMethodsInNonTestClass() throws Exception {
        new PmdAssert(
            "ManyMethods.java",
            Matchers.is(false),
            Matchers.containsString("TooManyMethods")
        ).assertOk();
    }
}
