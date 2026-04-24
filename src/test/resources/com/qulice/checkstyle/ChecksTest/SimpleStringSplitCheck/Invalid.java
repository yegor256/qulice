/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.SimpleStringSplitCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    public String[] first(final String txt) {
        return txt.split("xx");
    }

    public String[] second(final String txt) {
        return txt.split("xx", 1);
    }

    public String[] third(final String txt) {
        return txt.split(".");
    }

    public String[] fourth(final String txt) {
        return txt.split("\\s");
    }

    public String[] fifth(final String txt) {
        return txt.split("|");
    }
}
