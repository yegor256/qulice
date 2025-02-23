/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

/**
 * Represent a line range. For example, a Java method can be described by an
 * instance of this class. The alpha line could be the method definition and
 * the omega line could be the end closing bracket.
 *
 * @since 0.16
 */
public final class LineRange {

    /**
     * The first (alpha) line number in the range.
     */
    private final int alpha;

    /**
     * The last (omega) line number in the range.
     */
    private final int omega;

    /**
     * Default constructor.
     * @param first The alpha line number.
     * @param last The omega line number.
     */
    public LineRange(final int first, final int last) {
        this.alpha = first;
        this.omega = last;
    }

    /**
     * Is the given line number within range.
     * @param line The given line number to check.
     * @return True if the given line number is within this range.
     */
    public boolean within(final int line) {
        return line >= this.first() && line <= this.last();
    }

    /**
     * Is the given range entirely within the LineRange. Example, given a
     * LineRange of [10, 50], the given range of [12,48] should be within
     * side that. And the method should return true.
     * @param range The given LineRange to check.
     * @return True if the given is entirely within this LineRange.
     */
    public boolean within(final LineRange range) {
        return range.first() >= this.first()
            && range.last()  <= this.last();
    }

    /**
     * Get the alpha line number.
     * @return The alpha line number.
     */
    public int first() {
        return this.alpha;
    }

    /**
     * Get the omega line number.
     * @return The omega line number.
     */
    public int last() {
        return this.omega;
    }
}
