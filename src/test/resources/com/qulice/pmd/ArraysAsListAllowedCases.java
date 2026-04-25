/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.Arrays;
import java.util.List;

public final class ArraysAsListAllowedCases {
    public List<String> several() {
        return Arrays.asList("alpha", "beta", "gamma");
    }

    public List<byte[]> primitiveArray(final byte[] array) {
        return Arrays.asList(array);
    }
}
