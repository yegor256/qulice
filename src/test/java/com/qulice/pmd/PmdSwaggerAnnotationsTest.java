/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s tolerance of Swagger
 * annotations (no {@code RuleSetReferenceId} warnings must be
 * produced).
 * @since 0.25.1
 */
final class PmdSwaggerAnnotationsTest {

    @Test
    void allowsSwaggerAnnotations() throws Exception {
        new PmdAssert(
            "SwaggerApi.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("RuleSetReferenceId")
            )
        ).assertOk();
    }
}
