/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public final class UnnecessaryLocalCapturedTimestamp {

    public long timedWork() {
        final long begin = System.currentTimeMillis();
        doWork();
        return System.currentTimeMillis() - begin;
    }

    public long nanoTimer() {
        final long begin = System.nanoTime();
        doWork();
        return System.nanoTime() - begin;
    }

    public Instant capturedInstant() {
        final Instant moment = Instant.now();
        doWork();
        return moment;
    }

    public Date capturedDate() {
        final Date when = new Date();
        doWork();
        return when;
    }

    public String capturedUuid() {
        final UUID id = UUID.randomUUID();
        doWork();
        return id.toString();
    }

    public double capturedRandom() {
        final double rnd = Math.random();
        doWork();
        return rnd;
    }

    private void doWork() {
        // no-op
    }
}
