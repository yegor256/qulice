/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

/**
 * Test case for {@link PmdValidator}'s support for Java record
 * classes (parsed cleanly and not flagged as public-static-method
 * violations).
 * @since 0.25.1
 */
final class PmdRecordClassesTest {

    @Test
    @EnabledForJreRange(min = JRE.JAVA_21, max = JRE.JAVA_25)
    void allowsRecordClasses() throws Exception {
        new PmdAssert(
            "RecordParsed.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("Public static methods are prohibited")
            )
        ).assertOk();
    }
}
