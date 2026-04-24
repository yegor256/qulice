/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

/**
 * Exception thrown by a validator, if it fails.
 *
 * @since 0.3
 */
public final class ValidationException extends Exception {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 0x75298A7876D21470L;

    /**
     * Public ctor.
     * @param cause The cause of exception
     */
    public ValidationException(final Throwable cause) {
        this(null, cause);
    }

    /**
     * Public ctor.
     * @param text The text of the exception
     */
    public ValidationException(final String text) {
        this(text, null);
    }

    /**
     * Primary ctor.
     * @param text The text of the exception
     * @param cause The cause of exception
     */
    private ValidationException(final String text, final Throwable cause) {
        super(text, cause);
    }
}
