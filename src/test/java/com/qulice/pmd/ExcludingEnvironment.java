/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.spi.Environment;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Environment decorator that reports every file as excluded for any check.
 *
 * <p>Used to exercise the short-circuit path in {@link PmdValidator} when
 * all inputs match an exclude pattern. The default
 * {@link Environment.Mock} always answers {@code false} to
 * {@link Environment#exclude(String, String)}, so a decorator is required
 * to simulate a fully-excluded configuration.</p>
 *
 * @since 0.24.2
 */
@SuppressWarnings("PMD.TooManyMethods")
final class ExcludingEnvironment implements Environment {

    /**
     * Origin environment to delegate to.
     */
    private final Environment origin;

    /**
     * Ctor.
     * @param env Environment to delegate to
     */
    ExcludingEnvironment(final Environment env) {
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
        return true;
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
