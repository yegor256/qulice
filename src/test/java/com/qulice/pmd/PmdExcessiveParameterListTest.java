/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code ExcessiveParameterList} rule, which skips constructors but stays
 * active on methods, and which lets
 * {@code UnnecessaryWarningSuppression} report a suppression left on a
 * constructor.
 * Regression test for https://github.com/yegor256/qulice/issues/1763
 * and https://github.com/yegor256/qulice/issues/1775
 * @since 1.0
 */
final class PmdExcessiveParameterListTest {

    @Test
    void doesNotFireOnConstructorWithManyParameters() throws Exception {
        new PmdAssert(
            "ExcessiveParameterListInConstructor.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(ExcessiveParameterList)")
            )
        ).assertOk();
    }

    @Test
    void firesOnMethodWithManyParameters() throws Exception {
        new PmdAssert(
            "ExcessiveParameterListInMethod.java",
            Matchers.is(false),
            Matchers.containsString("(ExcessiveParameterList)")
        ).assertOk();
    }

    @Test
    void reportsRedundantSuppressionOnConstructor() throws Exception {
        new PmdAssert(
            "ExcessiveParameterListSuppressedInConstructor.java",
            Matchers.is(false),
            Matchers.containsString("(UnnecessaryWarningSuppression)")
        ).assertOk();
    }

    @Test
    void honoursSuppressionOnMethod() throws Exception {
        new PmdAssert(
            "ExcessiveParameterListSuppressedInMethod.java",
            Matchers.any(Boolean.class),
            Matchers.allOf(
                Matchers.not(
                    Matchers.containsString("(ExcessiveParameterList)")
                ),
                Matchers.not(
                    Matchers.containsString("(UnnecessaryWarningSuppression)")
                )
            )
        ).assertOk();
    }
}
