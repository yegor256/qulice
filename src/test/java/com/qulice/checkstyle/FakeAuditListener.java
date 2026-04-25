/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

/**
 * Fake Audit Listener that records every {@code addError} event into a
 * collaborating {@link AuditCollector}.
 *
 * <p>All other lifecycle hooks (audit started/finished, file
 * started/finished, exceptions) are intentionally no-ops because the
 * tests only care about the violations emitted by the Checkstyle
 * {@code Checker} run.</p>
 *
 * @since 0.27.0
 */
final class FakeAuditListener implements AuditListener {

    /**
     * Mocked collector.
     */
    private final AuditCollector collector;

    FakeAuditListener(final AuditCollector collect) {
        this.collector = collect;
    }

    @Override
    public void auditStarted(final AuditEvent event) {
        // intentionally empty: tests only assert on individual errors
    }

    @Override
    public void auditFinished(final AuditEvent event) {
        // intentionally empty: tests only assert on individual errors
    }

    @Override
    public void fileStarted(final AuditEvent event) {
        // intentionally empty: tests only assert on individual errors
    }

    @Override
    public void fileFinished(final AuditEvent event) {
        // intentionally empty: tests only assert on individual errors
    }

    @Override
    public void addError(final AuditEvent event) {
        this.collector.add(event);
    }

    @Override
    public void addException(
        final AuditEvent event,
        final Throwable throwable
    ) {
        // intentionally empty: tests only assert on individual errors
    }
}
