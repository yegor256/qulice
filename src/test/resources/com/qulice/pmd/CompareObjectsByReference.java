/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class CompareObjectsByReference {

    public boolean same(final Object left, final Object right) {
        return left == right;
    }
}
