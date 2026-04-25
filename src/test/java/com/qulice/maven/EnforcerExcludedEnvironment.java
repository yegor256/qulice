/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Environment decorator that reports the {@code "enforcer"} check as
 * always excluded.
 *
 * <p>Wraps a delegate {@link Environment} and forwards every call to
 * it, except {@link #exclude(String, String)}, which returns
 * {@code true} for the {@code "enforcer"} check name. Used by tests to
 * drive {@code EnforcerValidator} down its short-circuit path without
 * having to spin up a real enforcer plugin invocation.</p>
 *
 * @since 0.27.0
 */
final class EnforcerExcludedEnvironment implements Environment {

    /**
     * Origin environment.
     */
    private final Environment origin;

    EnforcerExcludedEnvironment(final Environment env) {
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
