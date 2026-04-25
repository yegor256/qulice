/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.Arrays;
import java.util.List;

public final class ArraysAsListSingleScalar {
    public List<Integer> primitive() {
        final int data = 3;
        return Arrays.asList(data);
    }
}
