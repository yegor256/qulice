/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for ReturnEmptyCollectionRatherThanNull.
 *
 * @since 0.19
 */
final class EmptyCollectionRuleTest {
    /**
     * Makes sure that empty collections not returned as null.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForNullCollection() throws Exception {
        new PmdAssert(
            "NullCollection.java",
            Matchers.is(false),
            Matchers.containsString(
                "Return an empty collection rather than null. (ReturnEmptyCollectionRatherThanNull)"
            )
        ).validate();
    }
}
