/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A test fake {@link ResourceValidator} that records how many times
 * {@code validate} was invoked and reports a configurable name and
 * rule count through {@link #name()} and {@link #rules()}. The validate
 * call always returns an empty collection, so {@code CheckMojo} treats
 * the run as clean.
 * @since 0.27.0
 */
final class FakeResourceValidator implements ResourceValidator {

    /**
     * Resource validator name.
     */
    private final String label;

    /**
     * How many rules this validator pretends to apply.
     */
    private final int total;

    /**
     * Method calls counter.
     */
    private final AtomicInteger cnt;

    FakeResourceValidator(final String name) {
        this(name, 0);
    }

    FakeResourceValidator(final String name, final int rules) {
        this.label = name;
        this.total = rules;
        this.cnt = new AtomicInteger(0);
    }

    @Override
    public Collection<Violation> validate(final Collection<File> files) {
        this.cnt.incrementAndGet();
        return Collections.emptyList();
    }

    @Override
    public String name() {
        return this.label;
    }

    @Override
    public int rules() {
        return this.total;
    }

    int count() {
        return this.cnt.get();
    }
}
