/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.pmd.rules.ProhibitPlainJunitAssertionsRule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator} covering JUnit assertion rules and
 * related test-class conventions.
 *
 * @since 0.25.0
 */
@SuppressWarnings("PMD.TooManyMethods")
final class PmdAssertionsTest {

    /**
     * Pattern using plain JUnit assertions.
     */
    private static final String PLAIN_ASSERTIONS =
        "Avoid using Plain JUnit assertions";

    /**
     * PmdValidator can prohibit plain JUnit assertion in import block like
     * import static org.junit.Assert.assert* import static
     * junit.framework.Assert.assert*.
     * <p>
     * Custom Rule {@link ProhibitPlainJunitAssertionsRule}
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void prohibitsStaticImportsPlainAssertionsInTests()
        throws Exception {
        final String file = "PlainJUnitAssertionStaticImportBlock.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            Matchers.containsString(
                PmdAssertionsTest.PLAIN_ASSERTIONS
            )
        ).validate();
    }

    /**
     * PmdValidator can prohibit plain JUnit assertion in test methods like
     * Assert.assertEquals.
     * <p>
     * Custom Rule {@link ProhibitPlainJunitAssertionsRule}
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void prohibitsPlainJunitAssertionsInTestMethods()
        throws Exception {
        final String file = "PlainJUnitAssertionTestMethod.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            Matchers.containsString(
                PmdAssertionsTest.PLAIN_ASSERTIONS
            )
        ).validate();
    }

    /**
     * PmdValidator can allow Assert.fail().
     * <p>
     * Custom Rule {@link ProhibitPlainJunitAssertionsRule}
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsAssertFail()
        throws Exception {
        final String file = "AllowAssertFail.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            Matchers.allOf(
                Matchers.not(
                    Matchers.containsString(
                        PmdAssertionsTest.PLAIN_ASSERTIONS
                    )
                ),
                Matchers.containsString("UnitTestContainsTooManyAsserts")
            )
        ).validate();
    }

    /**
     * PmdValidator does not report UnitTestContainsTooManyAsserts when a test
     * wraps an Assertions.assertThrows call inside an assertThat to verify the
     * thrown exception's message.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsAssertThrowsInsideAssertThat() throws Exception {
        new PmdAssert(
            "AssertThrowsWithMessageCheck.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("UnitTestContainsTooManyAsserts")
            )
        ).validate();
    }

    /**
     * PmdValidator still reports UnitTestContainsTooManyAsserts when a test
     * has multiple asserts in addition to an assertThrows call, because only
     * assertThrows is excluded from the count.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void reportsTooManyAssertsEvenWithAssertThrows() throws Exception {
        new PmdAssert(
            "TooManyAssertsWithAssertThrows.java",
            Matchers.is(false),
            Matchers.containsString("UnitTestContainsTooManyAsserts")
        ).validate();
    }

    /**
     * PmdValidator can allow only package private methods, marked with: Test,
     * RepeatedTest, TestFactory, TestTemplate or ParameterizedTest annotations.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void testShouldBePackagePrivate() throws Exception {
        new PmdAssert(
            "TestShouldBePackagePrivate.java",
            Matchers.is(false),
            Matchers.containsString("JUnit5TestShouldBePackagePrivate")
        ).validate();
    }

    /**
     * PmdValidator can allow only final JUnit3 test classes.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowJunitThirdTestClassToBeFinal() throws Exception {
        new PmdAssert(
            "Junit3TestClassShouldBeFinal.java",
            Matchers.is(false),
            Matchers.containsString("JUnitTestClassShouldBeFinal")
        ).validate();
    }

    /**
     * PmdValidator can allow only final JUnit4 test classes.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowJunitFourthTestClassToBeFinal() throws Exception {
        new PmdAssert(
            "Junit4TestClassShouldBeFinal.java",
            Matchers.is(false),
            Matchers.containsString("JUnitTestClassShouldBeFinal")
        ).validate();
    }

    /**
     * PmdValidator can allow only final JUnit5 test classes.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowJunitFifthTestClassToBeFinal() throws Exception {
        new PmdAssert(
            "Junit5TestClassShouldBeFinal.java",
            Matchers.is(false),
            Matchers.containsString("JUnitTestClassShouldBeFinal")
        ).validate();
    }

    /**
     * PmdValidator can allow only final Junit test classes.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowJunitTestClassToBeFinal() throws Exception {
        new PmdAssert(
            "JunitTestClassIsFinal.java",
            Matchers.is(false),
            Matchers.allOf(
                Matchers.not(
                    Matchers.containsString("JUnitTestClassShouldBeFinal")
                ),
                Matchers.containsString("UnitTestShouldIncludeAssert")
            )
        ).validate();
    }

    /**
     * PmdValidator can find assert() calls placed inside a lambda
     * body and not report UnitTestShouldIncludeAssert violation.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void findsAssertionInsideLambdaBody() throws Exception {
        new PmdAssert(
            "AssertInsideLambda.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("UnitTestShouldIncludeAssert")
            )
        ).validate();
    }

    /**
     * PmdValidator does not flag classes that use non-JUnit test conventions
     * like g4s8/oot, where a class ends with {@code *Test} but uses a
     * {@code public static void test()} entry point instead of
     * {@code @Test}-annotated methods.
     * Regression test for https://github.com/yegor256/qulice/issues/1064
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsNonJunitTestClassesWithStaticTestMethod() throws Exception {
        new PmdAssert(
            "OotStyleTest.java",
            Matchers.any(Boolean.class),
            Matchers.allOf(
                Matchers.not(
                    Matchers.containsString("TestClassWithoutTestCases")
                ),
                Matchers.not(
                    Matchers.containsString(
                        "UnitTestShouldUseTestAnnotation"
                    )
                )
            )
        ).validate();
    }

    /**
     * PmdValidator does not report JUnitAssertionsShouldIncludeMessage
     * (a.k.a. UnitTestAssertionsShouldIncludeMessage) when a test uses the
     * Hamcrest two-argument form
     * {@link org.hamcrest.MatcherAssert#assertThat(String, boolean)} where
     * the first argument already is the message.
     * Regression test for https://github.com/yegor256/qulice/issues/1315
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsMatcherAssertThatWithBooleanAndMessage() throws Exception {
        new PmdAssert(
            "MatcherAssertBooleanWithMessage.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString(
                    "UnitTestAssertionsShouldIncludeMessage"
                )
            )
        ).validate();
    }

    /**
     * PmdValidator still reports UnitTestAssertionsShouldIncludeMessage when
     * the two-argument form of
     * {@link org.hamcrest.MatcherAssert#assertThat(Object, org.hamcrest.Matcher)}
     * is used without a reason. Guards the fix for
     * https://github.com/yegor256/qulice/issues/1315 from over-suppressing.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void reportsMatcherAssertThatWithoutMessage() throws Exception {
        new PmdAssert(
            "MatcherAssertWithoutMessage.java",
            Matchers.is(false),
            Matchers.containsString(
                "UnitTestAssertionsShouldIncludeMessage"
            )
        ).validate();
    }
}
