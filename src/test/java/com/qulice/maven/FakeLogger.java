/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

/**
 * Fake Plexus {@link Logger} that buffers every log line in memory so
 * tests can assert what was logged.
 *
 * <p>Each level prefixes its message with the matching {@code [LEVEL]}
 * tag and appends to a single {@link StringBuilder}. {@link #toString}
 * returns the full buffered transcript, suitable for direct equality
 * checks in assertions.</p>
 *
 * @since 0.27.0
 */
@SuppressWarnings("PMD.AvoidStringBufferField")
final class FakeLogger extends AbstractLogger {

    /**
     * Log level tags.
     */
    private static final String[] TAGS = {
        "[DEBUG] ",
        "[INFO] ",
        "[WARNING] ",
        "[ERROR] ",
        "[FATAL ERROR] ",
    };

    /**
     * Logged messages.
     */
    private final StringBuilder messages;

    FakeLogger() {
        this(1, "fakelogger");
    }

    FakeLogger(final int threshold, final String name) {
        super(threshold, name);
        this.messages = new StringBuilder();
    }

    @Override
    public void debug(final String message, final Throwable throwable) {
        if (this.isDebugEnabled()) {
            this.messages.append(FakeLogger.TAGS[0].concat(message));
            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void info(final String message, final Throwable throwable) {
        if (this.isInfoEnabled()) {
            this.messages.append(FakeLogger.TAGS[1].concat(message));
            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void warn(final String message, final Throwable throwable) {
        if (this.isWarnEnabled()) {
            this.messages.append(FakeLogger.TAGS[2].concat(message));
            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void error(final String message, final Throwable throwable) {
        if (this.isErrorEnabled()) {
            this.messages.append(FakeLogger.TAGS[3].concat(message));
            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void fatalError(final String message, final Throwable throwable) {
        if (this.isFatalErrorEnabled()) {
            this.messages.append(FakeLogger.TAGS[4].concat(message));
            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        }
    }

    @Override
    public Logger getChildLogger(final String name) {
        return this;
    }

    @Override
    public String toString() {
        return this.messages.toString();
    }
}
