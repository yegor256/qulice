/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ProtectedMethodInFinalClassCheck;

import java.util.concurrent.Callable;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {

    protected int num = 1;

    public void foo() {}
    private void bar() {}

    private static class Bar {
        protected void valid() {}
    }

    private abstract static class Foo {
        protected void valid();
    }
}
