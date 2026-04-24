/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.SimpleStringSplitCheck;

import java.util.regex.Pattern;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    private static final Pattern PATTERN = Pattern.compile("xx");

    public String[] single(final String txt) {
        return txt.split("x");
    }

    public String[] limit(final String txt) {
        return txt.split("x", 2);
    }

    public String[] newline(final String txt) {
        return txt.split("\n");
    }

    public String[] escapedDot(final String txt) {
        return txt.split("\\.");
    }

    public String[] compiled(final String txt) {
        return Valid.PATTERN.split(txt);
    }

    public String[] variable(final String txt, final String sep) {
        return txt.split(sep);
    }
}
