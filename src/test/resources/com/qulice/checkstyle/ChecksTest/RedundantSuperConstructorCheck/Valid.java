/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.RedundantSuperConstructorCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    private final int param;
    public Valid(final int prm) {
        this.param = prm;
    }
    public Valid() {
        this(0);
    }
}

class ExtendsExplicit extends ArrayList<String> {
    private static final long serialVersionUID = 1L;
    ExtendsExplicit() {
        super();
    }
}

class ExtendsWithArgs extends ArrayList<String> {
    private static final long serialVersionUID = 1L;
    ExtendsWithArgs(final int capacity) {
        super(capacity);
    }
}

class ImplementsOnly implements Runnable {
    private final int code;
    ImplementsOnly(final int value) {
        this.code = value;
    }
    @Override
    public void run() {
    }
}

class WithAnonymous {
    private final Runnable task;
    WithAnonymous() {
        this.task = new Runnable() {
            @Override
            public void run() {
            }
        };
    }
}

class WithLocalClass {
    void make() {
        class Local extends ArrayList<String> {
            private static final long serialVersionUID = 1L;
            Local() {
                super(10);
            }
        }
        new Local();
    }
}

class CallsThis {
    private final List<String> items;
    CallsThis() {
        this(new ArrayList<>(0));
    }
    CallsThis(final List<String> list) {
        this.items = list;
    }
}
