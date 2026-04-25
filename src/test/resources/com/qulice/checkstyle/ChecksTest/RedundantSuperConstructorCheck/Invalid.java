/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.RedundantSuperConstructorCheck;

import java.io.Serializable;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid implements Serializable {
    private static final long serialVersionUID = 1L;
    public Invalid() {
        super();
    }
}

class InvalidNoInterface {
    InvalidNoInterface() {
        super();
    }
}

class InvalidWithFields implements Runnable {
    private final int value;
    InvalidWithFields(final int val) {
        super();
        this.value = val;
    }
    @Override
    public void run() {
    }
}

class InvalidNested {
    static final class Inner implements Runnable {
        Inner() {
            super();
        }
        @Override
        public void run() {
        }
    }
}
