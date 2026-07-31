/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import java.util.Collection;

/**
 * One {@code @checkstyle} suppression comment and the range it covers.
 *
 * <p>A comment like {@code @checkstyle TypeName (N lines)} tells the
 * suppression filters of {@code checks.xml} to drop violations of
 * {@code TypeName} on the lines it influences. The same holds for a pair
 * of {@code disable} and {@code enable} comments, whose range runs from
 * one to the other. A tag that influences
 * no violation of its check suppresses nothing and lingers as a lie about
 * the code, which is what {@link Suppressions} finds with the help of this
 * class.
 *
 * @since 1.0
 */
final class SuppressionTag {

    /**
     * Name of the check that the suppression targets.
     */
    private final String check;

    /**
     * Line of the comment, where the violation is reported.
     */
    private final int mark;

    /**
     * First line of the range the suppression influences.
     */
    private final int first;

    /**
     * Last line of the range the suppression influences.
     */
    private final int last;

    /**
     * Constructor.
     * @param check Name of the suppressed check
     * @param mark Line of the comment
     * @param first First line of the influence range
     * @param last Last line of the influence range
     * @checkstyle ParameterNumber (3 lines)
     */
    SuppressionTag(final String check, final int mark, final int first,
        final int last) {
        this.check = check;
        this.mark = mark;
        this.first = first;
        this.last = last;
    }

    /**
     * Name of the suppressed check.
     * @return The name
     */
    String check() {
        return this.check;
    }

    /**
     * Line of the comment, where a violation about it belongs.
     * @return Line number
     */
    int line() {
        return this.mark;
    }

    /**
     * Does this suppression influence none of these events?
     *
     * <p>The filters match the captured name against the fully qualified
     * class name of the check, which the name is a substring of, so the
     * same test decides here whether an event belongs to the suppressed
     * check.
     *
     * @param events Events collected with the nearby filters removed
     * @return True if no event of the check falls in the influence range
     */
    boolean unused(final Collection<AuditEvent> events) {
        return events.stream().noneMatch(this::covers);
    }

    /**
     * Does this suppression cover this event?
     * @param event Event to check
     * @return True if the event is of the check and in the range
     */
    private boolean covers(final AuditEvent event) {
        return event.getSourceName().contains(this.check)
            && event.getLine() >= this.first
            && event.getLine() <= this.last;
    }
}
