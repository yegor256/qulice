/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code MissingOverride} rule when an interface method is
 * implemented without the annotation (issue #770).
 * @since 0.25.1
 */
final class PmdMissingOverrideTest {

    @Test
    void reportsMissingOverrideOnInterfaceImplementation() throws Exception {
        new PmdAssert(
            "MissingOverrideOnInterfaceImpl.java",
            Matchers.is(false),
            Matchers.containsString("(MissingOverride)")
        ).assertOk();
    }
}
