/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.foo;

/**
 * Sample class holding a method nobody should call any more.
 * @since 1.0
 */
public final class Stale {

    /**
     * Method scheduled for removal.
     * @return Always one
     */
    @Deprecated(forRemoval = true)
    public static int old() {
        return 1;
    }
}
