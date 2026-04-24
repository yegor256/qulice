/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
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
                    new EnforcerValidatorTest.Excluded(new Environment.Mock()),
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

    /**
     * Environment decorator that reports the given check as excluded.
     * @since 0.70.0
     */
    private static final class Excluded implements Environment {

        /**
         * Origin environment.
         */
        private final Environment origin;

        Excluded(final Environment env) {
            this.origin = env;
        }

        @Override
        public File basedir() {
            return this.origin.basedir();
        }

        @Override
        public File tempdir() {
            return this.origin.tempdir();
        }

        @Override
        public File outdir() {
            return this.origin.outdir();
        }

        @Override
        public String param(final String name, final String value) {
            return this.origin.param(name, value);
        }

        @Override
        public ClassLoader classloader() {
            return this.origin.classloader();
        }

        @Override
        public Collection<String> classpath() {
            return this.origin.classpath();
        }

        @Override
        public Collection<File> files(final String pattern) {
            return this.origin.files(pattern);
        }

        @Override
        public boolean exclude(final String check, final String name) {
            return "enforcer".equals(check);
        }

        @Override
        public Collection<String> excludes(final String checker) {
            return this.origin.excludes(checker);
        }

        @Override
        public Charset encoding() {
            return this.origin.encoding();
        }
    }
}
