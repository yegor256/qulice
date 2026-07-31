/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ParameterNumberCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    private final int first;
    private final int second;
    public Invalid(final int one, final int two, final int three, final int four) {
        this.first = one + two;
        this.second = three + four;
    }
    public void update(final int one, final int two, final int three, final int four) {
        System.out.println(one + two + three + four);
    }
}

class OnlyConstants {
    private static final int FIRST = 1;
    private static final int SECOND = 2;
    private static final int THIRD = 3;
    private static final int FOURTH = 4;
    private final int total;
    OnlyConstants(final int one, final int two, final int three, final int four) {
        this.total = one + two + three + four;
    }
}
