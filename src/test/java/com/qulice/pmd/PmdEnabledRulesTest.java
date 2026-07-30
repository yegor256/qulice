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
 * Tests for rules that PMD ships and Qulice enables.
 * @since 0.25.1
 * @checkstyle MethodsOrderCheck (40 lines)
 */
final class PmdEnabledRulesTest {

    @ParameterizedTest
    @MethodSource("parameters")
    void enablesRules(final String rule) throws Exception {
        new PmdAssert(
            String.format("%s.java", rule),
            Matchers.is(false),
            Matchers.containsString(String.format("(%s)", rule))
        ).assertOk();
    }

    static Collection<String[]> parameters() {
        return Arrays.asList(
            new String[][] {
                {"HardCodedCryptoKey"},
                {"InsecureCryptoIv"},
                {"LiteralsFirstInComparisons"},
                {"UseDiamondOperator"},
                {"LinguisticNaming"},
                {"NonSerializableClass"},
            }
        );
    }
}
