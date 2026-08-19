/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.google.common.base.Predicate;

/**
 * Predicate to determine if a given line is within range of any of
 * the line ranges.
 * @since 0.1
 */
final class LineWithAny implements Predicate<LineRange> {

    /**
     * The given line.
     */
    private final int given;

    /**
     * Default constructor.
     * @param line The given line to check against all the line ranges
     */
    LineWithAny(final int line) {
        this.given = line;
    }

    @Override
    public boolean apply(final LineRange range) {
        return range != null && range.within(this.given);
    }
}
