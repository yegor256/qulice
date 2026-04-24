/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.jcabi.matchers.RegexMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s enforcement of the qulice
 * constructor rules: fields must be initialized either outside the
 * constructor or in a single constructor, and constructors may only
 * delegate or initialize fields.
 * @since 0.25.1
 */
final class PmdConstructorTest {

    /**
     * Pattern for non-constructor field initialization.
     */
    private static final String NO_CON_INIT =
        "%s\\[\\d+-\\d+\\]: Avoid doing field initialization outside constructor.";

    /**
     * Pattern for multiple constructors field initialization.
     */
    private static final String MULT_CON_INIT =
        "%s\\[\\d+-\\d+\\]: Avoid field initialization in several constructors.";

    /**
     * Pattern for code in constructor other than field init or delegation.
     */
    private static final String CODE_IN_CON =
        "%s\\[\\d+-\\d+\\]: Only field initialization or call to other constructors in a constructor";

    @Test
    void allowsFieldInitializationWhenConstructorIsMissing() throws Exception {
        final String file = "FieldInitNoConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PmdConstructorTest.NO_CON_INIT, file)
                )
            )
        ).assertOk();
    }

    @Test
    void forbidsFieldInitializationWhenConstructorExists() throws Exception {
        final String file = "FieldInitConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdConstructorTest.NO_CON_INIT, file)
            )
        ).assertOk();
    }

    @Test
    void allowsStaticFieldInitializationWhenConstructorExists()
        throws Exception {
        final String file = "StaticFieldInitConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PmdConstructorTest.NO_CON_INIT, file)
                )
            )
        ).assertOk();
    }

    @Test
    void forbidsFieldInitializationInSeveralConstructors() throws Exception {
        final String file = "FieldInitSeveralConstructors.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdConstructorTest.MULT_CON_INIT, file)
            )
        ).assertOk();
    }

    @Test
    void allowsFieldInitializationInOneConstructor() throws Exception {
        final String file = "FieldInitOneConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PmdConstructorTest.MULT_CON_INIT, file)
                )
            )
        ).assertOk();
    }

    @Test
    void forbidsCodeInConstructor() throws Exception {
        final String file = "CodeInConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdConstructorTest.CODE_IN_CON, file)
            )
        ).assertOk();
    }

    @Test
    void allowsLambdaInConstructor() throws Exception {
        final String file = "LambdaInConstructor.java";
        new PmdAssert(
            file,
            new IsEqual<>(true),
            new IsNot<>(
                RegexMatchers.containsPattern(
                    String.format(PmdConstructorTest.CODE_IN_CON, file)
                )
            )
        ).assertOk();
    }

    @Test
    void acceptsCallToConstructorInConstructor() throws Exception {
        final String file = "CallToConstructorInConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PmdConstructorTest.CODE_IN_CON, file)
                )
            )
        ).assertOk();
    }
}
