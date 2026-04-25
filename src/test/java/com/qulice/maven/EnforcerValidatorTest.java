/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link EnforcerValidator} class.
 * @since 0.70.0
 */
final class EnforcerValidatorTest {

    /**
     * EnforcerValidator can skip validation when the enforcer
     * check is excluded.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void skipsWhenEnforcerIsExcluded() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> new EnforcerValidator().validate(
                new MavenEnvironment.Wrap(
                    new EnforcerExcludedEnvironment(new Environment.Mock()),
                    new MavenEnvironmentMocker().mock()
                )
            ),
            "Enforcer validator must not invoke executor when check is excluded"
        );
    }

    /**
     * EnforcerValidator attempts to execute the plugin when the check
     * is not excluded.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void invokesExecutorWhenNotExcluded() throws Exception {
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> new EnforcerValidator()
                .validate(new MavenEnvironmentMocker().mock()),
            "Enforcer validator must call executor when check is not excluded"
        );
    }
}
