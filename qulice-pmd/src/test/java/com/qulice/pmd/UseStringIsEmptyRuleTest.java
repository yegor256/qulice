/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matcher;
import org.hamcrest.core.CombinableMatcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.hamcrest.text.IsEmptyString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link com.qulice.pmd.rules.UseStringIsEmptyRule}.
 * @since 0.18
 */
final class UseStringIsEmptyRuleTest {

    /**
     * UseStringIsEmpty can detect when used String.length(), when checking for
     * empty string.
     * @param file File name.
     * @throws Exception If something goes wrong.
     */
    @ParameterizedTest
    @ValueSource(
        strings = {
            "StringLengthGreaterThanZero.java",
            "StringLengthGreaterOrEqualZero.java",
            "StringLengthGreaterOrEqualOne.java",
            "StringLengthLessThanOne.java",
            "StringLengthLessOrEqualZero.java",
            "StringLengthEqualsZero.java",
            "StringLengthNotEqualsZero.java"
        }
    )
    void detectLengthComparisons(final String file) throws Exception {
        new PmdAssert(
            file, new IsEqual<>(false),
            new CombinableMatcher<>(containsMatcher(file, 20))
                .and(containsMatcher(file, 24))
                .and(containsMatcher(file, 28))
                .and(containsMatcher(file, 32))
                .and(containsMatcher(file, 36))
        ).validate();
    }

    /**
     * UseStringIsEmpty not detect when used String[].length, when checking for
     *  empty string.
     * @throws Exception If something goes wrong.
     */
    @Test
    void notDetectOnArrayOfStrings() throws Exception {
        new PmdAssert(
            "ArrayOfStringsLengthGreaterThanZero.java",
            new IsEqual<>(true),
            IsEmptyString.emptyString()
        ).validate();
    }

    /**
     * Constructs StringContains matcher for error message.
     * @param file File name.
     * @param line Line number.
     * @return StringContains matcher.
     */
    private static Matcher<String> containsMatcher(
        final String file, final int line
    ) {
        final String message =
            "Use String.isEmpty() when checking for empty string";
        return new StringContains(
            String.format(
                "PMD: %1$s[%2$d-%2$d]: %3$s (UseStringIsEmptyRule)",
                file, line, message
            )
        );
    }
}
