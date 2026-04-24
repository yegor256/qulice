/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of PMD's
 * clone-method rules: {@code CloneMethodMustBePublic} and
 * {@code CloneMethodReturnTypeMustMatchClassName}.
 * @since 0.25.1
 */
final class PmdCloneMethodTest {

    @Test
    void forbidsNonPublicCloneMethod() throws Exception {
        new PmdAssert(
            "CloneMethodMustBePublic.java",
            Matchers.is(false),
            Matchers.containsString("(CloneMethodMustBePublic)")
        ).assertOk();
    }

    @Test
    void forbidsCloneMethodReturnTypeNotMatchingClassName() throws Exception {
        new PmdAssert(
            "CloneMethodReturnTypeMustMatchClassName.java",
            Matchers.is(false),
            Matchers.containsString("(CloneMethodReturnTypeMustMatchClassName)")
        ).assertOk();
    }
}
