/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.StaticAccessViaInstanceCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    private static final int NUM = 42;
    private final int field;

    public Valid(final int prm) {
        this.field = prm;
    }

    public int doJob() {
        return Valid.helper() + this.field + Valid.NUM;
    }

    public int another() {
        final int local = Valid.NUM;
        return local + Valid.helper();
    }

    private static int helper() {
        return 1;
    }
}
