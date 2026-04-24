/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s rule prohibiting public
 * static methods, with the exceptions for {@code public static
 * void main} and JUnit lifecycle/parameterization hooks.
 * @since 0.25.1
 */
final class PmdPublicStaticMethodTest {

    /**
     * Error message for prohibited public static methods.
     */
    private static final String STATIC_METHODS =
        "Public static methods are prohibited";

    @Test
    void prohibitsPublicStaticMethods() throws Exception {
        new PmdAssert(
            "StaticPublicMethod.java",
            Matchers.is(false),
            Matchers.containsString(PmdPublicStaticMethodTest.STATIC_METHODS)
        ).assertOk();
    }

    @Test
    void allowsPublicStaticMainMethod() throws Exception {
        new PmdAssert(
            "StaticPublicVoidMainMethod.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString(PmdPublicStaticMethodTest.STATIC_METHODS)
            )
        ).assertOk();
    }

    @Test
    void allowsJunitFrameworkPublicStaticMethods() throws Exception {
        new PmdAssert(
            "JunitStaticPublicMethods.java",
            Matchers.is(false),
            Matchers.allOf(
                Matchers.not(
                    Matchers.containsString(PmdPublicStaticMethodTest.STATIC_METHODS)
                ),
                Matchers.containsString("UnitTestShouldIncludeAssert")
            )
        ).assertOk();
    }
}
