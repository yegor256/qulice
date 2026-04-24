/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.qulice.spi.Violation;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher for {@link Violation} produced by {@link CheckstyleValidator}
 * in unit tests. Matches by substring of the message, suffix of the file
 * path and, optionally, exact line number and check name.
 *
 * @since 0.1
 */
final class ViolationMatcher extends TypeSafeMatcher<Violation> {

    /**
     * Message to check.
     */
    private final String message;

    /**
     * File to check.
     */
    private final String file;

    /**
     * Expected line.
     */
    private final String line;

    /**
     * Check name.
     */
    private final String check;

    /**
     * Constructor.
     * @param message Message to check
     * @param file File to check
     * @param line Line to check
     * @param check Check name
     * @checkstyle ParameterNumber (3 lines)
     */
    ViolationMatcher(final String message, final String file,
        final String line, final String check) {
        super();
        this.message = message;
        this.file = file;
        this.line = line;
        this.check = check;
    }

    /**
     * Constructor.
     * @param message Message to check
     * @param file File to check
     */
    ViolationMatcher(final String message, final String file) {
        this(message, file, "", "");
    }

    @Override
    public boolean matchesSafely(final Violation item) {
        return item.message().contains(this.message)
            && item.file().endsWith(this.file)
            && this.lineMatches(item)
            && this.checkMatches(item);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("doesn't match");
    }

    /**
     * Check name matches.
     * @param item Item to check
     * @return True if check name matches
     */
    private boolean checkMatches(final Violation item) {
        return this.check.isEmpty()
            || !this.check.isEmpty() && item.name().equals(this.check);
    }

    /**
     * Check that given line matches.
     * @param item Item to check
     * @return True if line matches
     */
    private boolean lineMatches(final Violation item) {
        return this.line.isEmpty()
            || !this.line.isEmpty() && item.lines().equals(this.line);
    }
}
