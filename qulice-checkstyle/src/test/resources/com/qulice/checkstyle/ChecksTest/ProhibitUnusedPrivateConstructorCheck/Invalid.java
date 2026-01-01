/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ProhibitUnusedPrivateConstructorCheck;

/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {

    private final String s;

    public Invalid(String s) {
        this.s = s;
    }

    private Invalid(int x) {
        this(String.valueOf(x));
    }

    public static Invalid create() {
        return new Invalid("0");
    }

}
