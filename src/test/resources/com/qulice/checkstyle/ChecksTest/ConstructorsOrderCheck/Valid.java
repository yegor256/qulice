/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ConstructorsOrderCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    public Valid(String name) {
        this(name, 0);
    }
    public Valid(String name, int age) {
        this(name, age, false);
    }
    public Valid(String name, int age, boolean active) {
        System.out.println(name + age + active);
    }

    static class OnlyPrimary {
        OnlyPrimary(int number) {
            System.out.println(number);
        }
    }

    static class WithInnerDelegations {
        WithInnerDelegations() {
            this(0);
        }
        WithInnerDelegations(int number) {
            System.out.println(number);
        }
    }

    enum Color {
        RED(1), GREEN(2), BLUE(3);
        private final int code;
        Color(int value) {
            this.code = value;
        }
    }
}
