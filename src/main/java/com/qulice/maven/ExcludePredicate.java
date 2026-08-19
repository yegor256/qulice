/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Predicate;
import java.util.Collection;

/**
 * Predicate for excluded dependencies.
 * @since 0.1
 */
final class ExcludePredicate implements Predicate<String> {

    /**
     * List of excludes.
     */
    private final Collection<String> excludes;

    /**
     * Constructor.
     * @param excludes List of excludes
     */
    ExcludePredicate(final Collection<String> excludes) {
        this.excludes = excludes;
    }

    @Override
    public boolean apply(final String name) {
        boolean ignore = false;
        for (final String exclude : this.excludes) {
            if (name.startsWith(exclude)) {
                ignore = true;
                break;
            }
        }
        return ignore;
    }
}
