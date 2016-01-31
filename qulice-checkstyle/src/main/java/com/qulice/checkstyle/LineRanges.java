/**
 * Copyright (c) 2011-2016, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.checkstyle;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represent a set of LineRange objects. For example, an instance of this class
 * could represent all the line ranges for methods in a given Java source code
 * file.
 *
 * @author Jimmy Spivey (JimDeanSpivey@gmail.com)
 * @version $Id$
 */
public final class LineRanges {

    /**
     * ArrayList of line ranges.
     */
    private final transient Collection<LineRange> lines =
        new ArrayList<LineRange>(20);

    /**
     * Adds a line range to the internal hashset.
     * @param line The line range to add to the hash set
     */
    public void add(final LineRange line) {
        this.lines.add(line);
    }

    /**
     * Detects if the proposed line number is with any of the line ranges.
     * @param line The proposed line number to check
     * @return True if the proposed line number is within any line range.
     */
    public boolean inRange(final int line) {
        return !this.lines.isEmpty() && FluentIterable.from(this.lines)
            .anyMatch(new LineRanges.LineWithAny(line));
    }

    /**
     * Predicate to determine if a proposed line is within range of any of
     * the line ranges.
     */
    private static final class LineWithAny implements Predicate<LineRange> {

        /**
         * The proposed line.
         */
        private final transient int line;

        /**
         * Default constructor.
         * @param row The proposed line to check against all the line ranges.
         */
        private LineWithAny(final int row) {
            this.line = row;
        }

        @Override
        public boolean apply(final LineRange range) {
            return range != null && range.inRange(this.line);
        }
    }
}
