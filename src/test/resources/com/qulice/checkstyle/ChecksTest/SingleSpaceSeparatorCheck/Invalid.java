/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.SingleSpaceSeparatorCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    private final int value;
    private  final  String  name = "x";

    public Invalid(final  int prm) {
        this.value = prm;
    }

    public int doJob() {
        final int local  = 1;
        return local + this.value;
    }
}
