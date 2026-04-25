/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.text.IsEmptyString;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link com.qulice.pmd.rules.UseCollectionsSingletonListRule}.
 * @since 0.26.0
 */
final class UseCollectionsSingletonListRuleTest {

    /**
     * Error message produced by the rule.
     */
    private static final String MESSAGE =
        "Use Collections.singletonList instead of Arrays.asList with a single non-array argument";

    @Test
    void detectsArraysAsListWithSingleScalar() throws Exception {
        new PmdAssert(
            "ArraysAsListSingleScalar.java",
            new IsEqual<>(false),
            Matchers.containsString(
                UseCollectionsSingletonListRuleTest.MESSAGE
            )
        ).assertOk();
    }

    @Test
    void allowsMultipleArgumentsAndArrayArgument() throws Exception {
        new PmdAssert(
            "ArraysAsListAllowedCases.java",
            new IsEqual<>(true),
            IsEmptyString.emptyString()
        ).assertOk();
    }
}
