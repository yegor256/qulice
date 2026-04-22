/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ConstructorsOrderCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    public Invalid(String name, int age) {
        System.out.println(name + age);
    }
    public Invalid(String name) {
        this(name, 0);
    }

    static class Inner {
        Inner(int number) {
            System.out.println(number);
        }
        Inner() {
            this(0);
        }
        Inner(String text) {
            this();
        }
    }

    static class MixedOrder {
        MixedOrder(String text) {
            this(text, 0);
        }
        MixedOrder(String text, int number) {
            System.out.println(text + number);
        }
        MixedOrder() {
            this("");
        }
    }
}
