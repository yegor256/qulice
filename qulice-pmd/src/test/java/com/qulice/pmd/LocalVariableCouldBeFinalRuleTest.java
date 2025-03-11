/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;

/**
 * Test case for LocalVariableCouldBeFinal.
 *
 * @since 0.18
 */
final class LocalVariableCouldBeFinalRuleTest {

    /**
     * LocalVariableCouldBeFinal can detect when variable is not
     *  final and shows correct message.
     *
     * @throws Exception If something goes wrong
     */
    @Test
    void detectLocalVariableCouldBeFinal() throws Exception {
        new PmdAssert(
            "LocalVariableCouldBeFinal.java",
            new IsEqual<>(false),
            new StringStartsWith(
                String.join(
                    " ",
                    "PMD: LocalVariableCouldBeFinal.java[10-10]:",
                    "Local variable 'nonfinal' could be declared final",
                    "(LocalVariableCouldBeFinal)"
                )
            )
        ).validate();
    }
}
