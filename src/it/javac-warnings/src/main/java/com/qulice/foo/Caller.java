/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.foo;

/**
 * Sample class whose only defect is a plain {@code javac} warning, with no
 * ErrorProne bug pattern anywhere in sight.
 * @since 1.0
 */
public final class Caller {

    /**
     * Call the stale method and earn the {@code [removal]} warning.
     * @return Whatever {@link Stale#old()} returns
     */
    public int call() {
        return Stale.old();
    }
}
