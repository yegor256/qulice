/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a set of LineRange objects. For example, an instance of this class
 * could represent all the line ranges for methods in a given Java source code
 * file.
 *
 * @since 0.16
 */
public final class LineRanges {

    /**
     * ArrayList of line ranges.
     */
    private final LineRanges.LocalCollection lines =
        new LineRanges.LocalCollection();

    /**
     * Adds a line range to the collection.
     * @param line The line range to add to the collection
     */
    public void add(final LineRange line) {
        this.lines.collection().add(line);
    }

    /**
     * Returns an iterator for this collection.
     * @return Iterator pointing to the internal collections elements.
     */
    public Iterator<LineRange> iterator() {
        return this.lines.collection().iterator();
    }

    /**
     * Detects if the given line number is within any of the line ranges.
     * @param line The given line number to check
     * @return True if the given line number is within any line range.
     */
    public boolean inRange(final int line) {
        return !this.lines.collection().isEmpty()
            && FluentIterable.from(this.lines.collection())
            .anyMatch(new LineRanges.LineWithAny(line));
    }

    /**
     * Gets the subset of LineRanges that are within all given ranges. Does
     * not return null; instead, returns empty range if there are no matches.
     * @param ranges The ranges to filter on.
     * @return Returns all LineRange elements that are within range.
     */
    public LineRanges within(final LineRanges ranges) {
        final LineRanges result = new LineRanges();
        final Iterator<LineRange> iterator = ranges.iterator();
        while (iterator.hasNext()) {
            final LineRange next = iterator.next();
            for (final LineRange line : this.lines.collection()) {
                if (next.within(line)) {
                    result.add(line);
                }
            }
        }
        return result;
    }

    /**
     * Clears the collection.
     */
    public void clear() {
        this.lines.collection().clear();
    }

    /**
     * Predicate to determine if a given line is within range of any of
     * the line ranges.
     *
     * @since 0.1
     */
    private static final class LineWithAny implements Predicate<LineRange> {

        /**
         * The given line.
         */
        private final int given;

        /**
         * Default constructor.
         * @param line The given line to check against all the line ranges.
         */
        private LineWithAny(final int line) {
            this.given = line;
        }

        @Override
        public boolean apply(final LineRange range) {
            return range != null && range.within(this.given);
        }
    }

    /**
     * Thread-safe collection of line ranges.
     *
     * @since 0.1
     */
    private static final class LocalCollection
        extends ThreadLocal<Collection<LineRange>> {

        /**
         * Internal Collection.
         */
        private final transient Collection<LineRange> ranges =
            new ArrayList<>(20);

        /**
         * Get the collection specific to the current thread only.
         * @return The collection for this thread.
         */
        public Collection<LineRange> collection() {
            return this.ranges;
        }
    }
}
