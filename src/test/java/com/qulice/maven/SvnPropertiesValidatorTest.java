/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import org.junit.jupiter.api.Test;

/**
 * Test case for {@link SvnPropertiesValidator}.
 * @since 0.3
 */
final class SvnPropertiesValidatorTest {

    /**
     * Let's simulate the property reading request.
     * @throws Exception If something goes wrong
     */
    @Test
    void testSimulatesSvnPropgetRequest() throws Exception {
        final MavenValidator validator = new SvnPropertiesValidator();
        final MavenEnvironment env = new MavenEnvironmentMocker().mock();
        validator.validate(env);
    }
}
