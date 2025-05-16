/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
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
     * @param text The text of the exception
     * @param args Optional arguments for String.format()
     */
    public ValidationException(final String text, final Object... args) {
        if (text == null) {
            throw new IllegalArgumentException("Exception message cannot be null");
        }
        super(String.format(text, args));
    }

    /**
     * Public ctor.
     * @param cause The cause of exception
     */
    public ValidationException(final Throwable cause) {
        super(cause);
    }

}
