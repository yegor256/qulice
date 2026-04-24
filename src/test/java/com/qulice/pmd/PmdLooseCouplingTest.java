/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of the
 * {@code LooseCoupling} rule: fields, parameters and return
 * types declared with concrete collection implementations
 * such as {@code ConcurrentHashMap} or {@code HashMap} must
 * be reported (issue #734).
 * @since 0.25.1
 */
final class PmdLooseCouplingTest {

    @Test
    void reportsConcreteCollectionTypes() throws Exception {
        new PmdAssert(
            "ConcreteCollectionTypes.java",
            Matchers.is(false),
            Matchers.allOf(
                Matchers.containsString("(LooseCoupling)"),
                Matchers.containsString("ConcurrentHashMap"),
                Matchers.containsString("HashMap")
            )
        ).assertOk();
    }
}
