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
 * does not push the class past the threshold, and which counts
 * only public and protected methods that the class itself
 * introduces.
 * Regression test for https://github.com/yegor256/qulice/issues/1605,
 * https://github.com/yegor256/qulice/issues/1647,
 * https://github.com/yegor256/qulice/issues/1656
 * and https://github.com/yegor256/qulice/issues/1667
 * @since 1.0
 */
final class PmdTooManyMethodsTest {

    @ParameterizedTest
    @ValueSource(
        strings = {
            "ManyMethodsTest.java",
            "ManyMethodsIT.java",
            "ManyMethodsNestedTest.java",
            "ManyMethodsChecks.java",
            "ManyPublicMethodsTest.java"
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

    @Test
    void reportsTooManyProtectedMethods() throws Exception {
        new PmdAssert(
            "ManyProtectedMethods.java",
            Matchers.is(false),
            Matchers.containsString("TooManyMethods")
        ).assertOk();
    }

    @Test
    void ignoresPrivateMethods() throws Exception {
        new PmdAssert(
            "ManyPrivateMethods.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("TooManyMethods")
            )
        ).assertOk();
    }

    @Test
    void ignoresOverriddenMethods() throws Exception {
        new PmdAssert(
            "ManyOverriddenMethods.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("TooManyMethods")
            )
        ).assertOk();
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "JnaLibrary.java",
            "JnaQualifiedLibrary.java",
            "JnaDirectLibrary.java"
        }
    )
    void allowsManyMethodsInJnaBinding(final String file) throws Exception {
        new PmdAssert(
            file,
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("TooManyMethods")
            )
        ).assertOk();
    }

    @Test
    void reportsTooManyMethodsInPlainInterface() throws Exception {
        new PmdAssert(
            "ManyMethodsInterface.java",
            Matchers.is(false),
            Matchers.containsString("TooManyMethods")
        ).assertOk();
    }

    @Test
    void reportsRedundantSuppressionInTestClass() throws Exception {
        new PmdAssert(
            "ManyMethodsSuppressedTest.java",
            Matchers.is(false),
            Matchers.containsString("UnnecessaryWarningSuppression")
        ).assertOk();
    }
}
