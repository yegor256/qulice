/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.StaticAccessViaInstanceCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    private static final int NUM = 42;
    private final int field;

    public Invalid(final int prm) {
        this.field = prm;
    }

    public int doJob() {
        final int one = this.helper();
        final int two = this.NUM;
        final int three = this.field;
        return one + two + three;
    }

    public int another() {
        return this.NUM + this.helper();
    }

    private static int helper() {
        return 1;
    }
}
