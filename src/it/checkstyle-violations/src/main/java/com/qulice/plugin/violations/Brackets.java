/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.plugin.violations;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Validation of bracket structure check.
 */
public final class Brackets {

    /**
     * Simple method.
     */
    public void wrongBrackets() {
        new Foo(null, new int[]{1},
            null
        );
        new Foo(new String(""),
            null, null
        );
        this.call(null,
            null
        );
        final AtomicInteger atom = new AtomicInteger(
            1);
    }

    private void call(final String start, final String end) {
        // do nothing
    }

    /**
     * Check brackets structure.
     */
    private final class Foo {

        /**
         * Constructor.
         * @param start First param.
         * @param list Second param.
         * @param rest Last param.
         */
        public Foo(final String start, final int[] list, final String rest) {
            // ignore
        }
    }
}
