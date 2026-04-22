/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ConstructorsCodeFreeCheck;

import java.util.regex.Pattern;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    private final int param;
    private final Pattern pattern;
    private final int bar;

    public Invalid(final int prm) {
        this.param = prm;
        this.pattern = Pattern.compile(".*");
        this.bar = Invalid.makeBar();
    }

    public Invalid(final String text) {
        this.param = Integer.parseInt(text);
        this.pattern = Pattern.compile(text);
        this.bar = 0;
    }

    public Invalid(final double value) {
        this.param = (int) value;
        this.pattern = null;
        this.bar = 0;
        System.out.println(value);
    }

    private static int makeBar() {
        return 42;
    }
}
