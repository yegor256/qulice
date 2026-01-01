/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ProhibitUnusedPrivateConstructorCheck;
/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {

    private final int i;

    public Valid(final String s) {
        this(s.length());
    }

    private Valid(int i) {
        this.i = i;
    }

}
