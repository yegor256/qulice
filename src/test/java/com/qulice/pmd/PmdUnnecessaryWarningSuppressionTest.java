/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s fix for #1534: the
 * {@code UnnecessaryWarningSuppression} rule must not report
 * {@code @SuppressWarnings("PMD.UnnecessaryWarningSuppression")}
 * as an unused suppression of itself.
 * @since 0.25.1
 */
final class PmdUnnecessaryWarningSuppressionTest {

    @Test
    void doesNotCatchUnnecessaryWarningSuppressionOnItself() throws Exception {
        new PmdAssert(
            "UnnecessaryWarningSuppressionOnItself.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("UnnecessaryWarningSuppression")
            )
        ).assertOk();
    }
}
