/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code TooManyFields} rule, which skips classes extending
 * {@code AbstractMojo} but stays active on plain classes, and which lets
 * {@code UnnecessaryWarningSuppression} report a suppression left on a
 * Mojo.
 * Regression test for https://github.com/yegor256/qulice/issues/1767
 * and https://github.com/yegor256/qulice/issues/1775
 * @since 1.0
 */
final class PmdTooManyFieldsTest {

    @Test
    void doesNotFireOnMojoWithManyFields() throws Exception {
        new PmdAssert(
            "TooManyFieldsInMojo.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(TooManyFields)")
            )
        ).assertOk();
    }

    @Test
    void firesOnPlainClassWithManyFields() throws Exception {
        new PmdAssert(
            "TooManyFieldsInPlainClass.java",
            Matchers.is(false),
            Matchers.containsString("(TooManyFields)")
        ).assertOk();
    }

    @Test
    void ignoresConstants() throws Exception {
        new PmdAssert(
            "TooManyFieldsInConstants.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(TooManyFields)")
            )
        ).assertOk();
    }

    @Test
    void reportsRedundantSuppressionOnMojo() throws Exception {
        new PmdAssert(
            "TooManyFieldsSuppressedInMojo.java",
            Matchers.is(false),
            Matchers.containsString("(UnnecessaryWarningSuppression)")
        ).assertOk();
    }

    @Test
    void honoursSuppressionOnPlainClass() throws Exception {
        new PmdAssert(
            "TooManyFieldsSuppressedInPlainClass.java",
            Matchers.any(Boolean.class),
            Matchers.allOf(
                Matchers.not(Matchers.containsString("(TooManyFields)")),
                Matchers.not(
                    Matchers.containsString("(UnnecessaryWarningSuppression)")
                )
            )
        ).assertOk();
    }
}
