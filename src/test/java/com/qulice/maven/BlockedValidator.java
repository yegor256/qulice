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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A test fake {@link ResourceValidator} that signals through a latch
 * the moment its {@code validate} call begins, then blocks the calling
 * thread indefinitely on {@code Thread.sleep}.
 *
 * <p>Used by tests that exercise the timeout/interruption behaviour of
 * {@code CheckMojo}: tests use {@link #await()} to wait until validation
 * has actually entered the validator before driving the timeout, and
 * {@link #count()} to confirm the validator was invoked exactly once.</p>
 *
 * @since 0.27.0
 */
final class BlockedValidator implements ResourceValidator {

    /**
     * Method calls counter.
     */
    private final AtomicInteger cnt;

    /**
     * Latch to signal when validation starts.
     */
    private final CountDownLatch latch;

    BlockedValidator() {
        this.cnt = new AtomicInteger(0);
        this.latch = new CountDownLatch(1);
    }

    @Override
    public Collection<Violation> validate(final Collection<File> ignore) {
        this.cnt.incrementAndGet();
        this.latch.countDown();
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return Collections.emptyList();
    }

    @Override
    public String name() {
        return "blocked forever";
    }

    int count() {
        return this.cnt.get();
    }

    void await() {
        try {
            this.latch.await();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
