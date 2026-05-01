/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ConstructorsCodeFreeCheck;

import java.util.Arrays;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class ValidDefensiveCopy {
    private final byte[] data;
    private final int[] codes;
    private final long[] points;
    private final char[] letters;

    public ValidDefensiveCopy(final byte[] bytes) {
        this.data = Arrays.copyOf(bytes, bytes.length);
        this.codes = null;
        this.points = null;
        this.letters = null;
    }

    public ValidDefensiveCopy(final int[] ints) {
        this.data = null;
        this.codes = Arrays.copyOf(ints, ints.length);
        this.points = null;
        this.letters = null;
    }

    public ValidDefensiveCopy(final long[] longs) {
        this.data = null;
        this.codes = null;
        this.points = longs.clone();
        this.letters = null;
    }

    public ValidDefensiveCopy(final char[] chars) {
        this.data = null;
        this.codes = null;
        this.points = null;
        this.letters = chars.clone();
    }

    static final class Nested {
        private final byte[] payload;
        Nested(final byte[] bytes) {
            this(Arrays.copyOf(bytes, bytes.length));
        }
        Nested(final byte[] bytes, final int unused) {
            this.payload = Arrays.copyOf(bytes, bytes.length);
        }
    }
}
