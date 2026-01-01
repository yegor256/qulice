/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import java.util.Arrays;
import java.util.Collection;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for disabled rules.
 * @since 0.16
 * @checkstyle MethodsOrderCheck (77 lines)
 */
final class PmdDisabledRulesTest {

    @ParameterizedTest
    @MethodSource("parameters")
    void disablesRules(final String rule) throws Exception {
        new PmdAssert(
            String.format("%s.java", rule),
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString(
                    String.format("(%s)", rule)
                )
            )
        ).validate();
    }

    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Collection<String[]> parameters() {
        return Arrays.asList(
            new String[][] {
                {"UseConcurrentHashMap"},
                {"DoNotUseThreads"},
                {"AvoidUsingVolatile"},
                {"DefaultPackage"},
                {"ExcessiveImports"},
                {"PositionLiteralsFirstInComparisons"},
                {"MissingSerialVersionUID"},
                {"CallSuperInConstructor"},
            }
        );
    }

}
