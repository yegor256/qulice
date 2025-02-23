/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ValidatorsProvider} class.
 * @since 0.3
 */
final class DefaultValidatorsProviderTest {

    @Test
    void producesCollectionOfValidators() throws Exception {
        MatcherAssert.assertThat(
            "internal validators should be returned",
            new DefaultValidatorsProvider(new Environment.Mock())
                .internal().size(),
            Matchers.greaterThan(0)
        );
    }

    @Test
    @Disabled
    void producesCollectionOfExtValidators() throws Exception {
        MatcherAssert.assertThat(
            "external validators should be returned",
            new DefaultValidatorsProvider(new Environment.Mock())
                .external().size(),
            Matchers.greaterThan(0)
        );
    }

}
