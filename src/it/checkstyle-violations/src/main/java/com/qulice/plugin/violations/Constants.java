/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.plugin.violations;

/**
 * Validation of constants check.
 */
public final class Constants {

    private static final String ONCE = "test";

    private static final String TWICE = "test";

    public void print() {
        System.out.println(Constants.ONCE + Constants.TWICE);
    }

    public void anotherPrint() {
        System.out.println(Constants.TWICE);
    }
}
