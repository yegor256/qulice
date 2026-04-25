/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import com.qulice.spi.Validator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A test fake {@link Validator} that records how many times
 * {@link #validate(Environment)} was invoked and reports a configurable
 * name through {@link #name()}.
 * @since 0.27.0
 */
final class FakeValidator implements Validator {

    /**
     * Validator name.
     */
    private final String label;

    /**
     * Method calls counter.
     */
    private final AtomicInteger cnt;

    FakeValidator(final String name) {
        this.label = name;
        this.cnt = new AtomicInteger(0);
    }

    @Override
    public void validate(final Environment env) {
        this.cnt.incrementAndGet();
    }

    @Override
    public String name() {
        return this.label;
    }

    int count() {
        return this.cnt.get();
    }
}
