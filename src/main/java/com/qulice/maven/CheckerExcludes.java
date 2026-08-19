/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Function;
import javax.annotation.Nullable;

/**
 * Converts a checker exclude into exclude param.
 *
 * <p>E.g. "checkstyle:.*" will become ".*".
 *
 * @since 0.1
 */
final class CheckerExcludes implements Function<String, String> {

    /**
     * All checkers.
     */
    private static final String ALL = "*";

    /**
     * Name of checker.
     */
    private final String checker;

    /**
     * Constructor.
     * @param checker Name of checker
     */
    CheckerExcludes(final String checker) {
        this.checker = checker;
    }

    @Nullable
    @Override
    public String apply(@Nullable final String input) {
        String result = null;
        if (input != null) {
            final String[] exclude = input.split(":", 2);
            final String check = exclude[0];
            final boolean appropriate = CheckerExcludes.ALL.equals(check)
                || this.checker.equals(check);
            if (appropriate && exclude.length > 1) {
                result = exclude[1];
            }
        }
        return result;
    }
}
