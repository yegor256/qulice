/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.foo;

import org.apache.commons.lang3.StringUtils;

/**
 * Test class using a compile-time constant.
 * @since 1.0
 */
public final class Sample {

    /**
     * Index for "not found".
     */
    private static final int NOT_FOUND = StringUtils.INDEX_NOT_FOUND;

    /**
     * Ctor.
     */
    public Sample() {
        // nothing to initialize
    }

    /**
     * Return the "not found" constant.
     * @return The {@value #NOT_FOUND} index
     * @checkstyle NonStaticMethod (2 lines)
     */
    public int missing() {
        return Sample.NOT_FOUND;
    }
}
