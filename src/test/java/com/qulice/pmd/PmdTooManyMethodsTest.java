/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code TooManyMethods} rule, which is disabled for test
 * classes so that splitting asserts into separate {@code @Test}
 * methods (required by {@code UnitTestContainsTooManyAsserts})
 * does not push the class past the threshold.
 * Regression test for https://github.com/yegor256/qulice/issues/1605
 * and https://github.com/yegor256/qulice/issues/1647
 * @since 1.0
 */
final class PmdTooManyMethodsTest {

    @ParameterizedTest
    @ValueSource(
        strings = {
            "ManyMethodsTest.java",
            "ManyMethodsIT.java",
            "ManyMethodsNestedTest.java",
            "ManyMethodsChecks.java"
        }
    )
    void allowsManyMethodsInTestClass(final String file) throws Exception {
        new PmdAssert(
            file,
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
