/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code UnusedPrivateField} rule, including the suppression for
 * fields referenced through a fully-qualified outer-class path
 * inside a lambda (issue #1520).
 * @since 0.25.1
 */
final class PmdUnusedPrivateFieldTest {

    @Test
    void allowsPrivateStaticFieldAccessedViaFullyQualifiedName()
        throws Exception {
        new PmdAssert(
            "UnusedPrivateFieldInLambda.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(UnusedPrivateField)")
            )
        ).assertOk();
    }

    @Test
    void reportsTrulyUnusedPrivateField() throws Exception {
        new PmdAssert(
            "UnusedPrivateFieldTrulyUnused.java",
            Matchers.any(Boolean.class),
            Matchers.containsString("(UnusedPrivateField)")
        ).assertOk();
    }
}
