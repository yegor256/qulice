/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ConstructorsCodeFreeCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    private final int param;
    private final List<String> items;

    public Valid(final int prm) {
        this(prm, new ArrayList<>());
    }

    public Valid(final int prm, final List<String> list) {
        this.param = prm;
        this.items = list;
    }

    static final class DelegatingOnly {
        private final int val;
        DelegatingOnly() {
            this(0);
        }
        DelegatingOnly(final int value) {
            this.val = value;
        }
    }

    static final class WithSuper {
        private final int val;
        WithSuper(final int value) {
            super();
            this.val = value;
        }
    }

    enum Color {
        RED(1), GREEN(2), BLUE(3);
        private final int code;
        Color(final int value) {
            this.code = value;
        }
    }
}
