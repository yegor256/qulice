/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import java.util.LinkedList;
import java.util.List;
import org.cactoos.text.Joined;

/**
 * Mocked collector of checkstyle events used by ChecksTest.
 *
 * <p>Captures every {@link AuditEvent} emitted by a Checkstyle
 * {@code Checker} into an in-memory list, exposing helpers to count
 * events, look up a specific line/message pair, and render a debug
 * summary of all events for assertion failure messages.</p>
 *
 * @since 0.27.0
 */
final class AuditCollector {

    /**
     * List of events received.
     */
    private final List<AuditEvent> events = new LinkedList<>();

    void add(final AuditEvent event) {
        this.events.add(event);
    }

    /**
     * How many messages do we have?
     * @return Amount of messages reported
     */
    int eventCount() {
        return this.events.size();
    }

    /**
     * Do we have this message for this line?
     * @param line The number of the line
     * @param msg The message we're looking for
     * @return This message was reported for the give line?
     */
    boolean has(final Integer line, final String msg) {
        boolean has = false;
        for (final AuditEvent event : this.events) {
            if (event.getLine() == line && event.getMessage().equals(msg)) {
                has = true;
                break;
            }
        }
        return has;
    }

    /**
     * Returns full summary.
     * @return The test summary of all events
     */
    String summary() {
        final List<String> msgs = new LinkedList<>();
        for (final AuditEvent event : this.events) {
            msgs.add(
                String.format(
                    "%s:%s",
                    event.getLine(),
                    event.getMessage()
                )
            );
        }
        return new Joined("; ", msgs).toString();
    }
}
