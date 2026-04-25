/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A test fake {@link MavenValidator} that records how many times its
 * {@code validate(MavenEnvironment)} entry point was invoked, so tests
 * can assert that {@code CheckMojo} drove every internal validator
 * exactly once.
 * @since 0.27.0
 */
final class FakeMavenValidator implements MavenValidator {

    /**
     * Method calls counter.
     */
    private final AtomicInteger cnt;

    FakeMavenValidator() {
        this.cnt = new AtomicInteger(0);
    }

    @Override
    public void validate(final MavenEnvironment env) {
        this.cnt.incrementAndGet();
    }

    int count() {
        return this.cnt.get();
    }
}
