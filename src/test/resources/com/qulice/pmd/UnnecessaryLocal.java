/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class UnnecessaryLocal {

    public int returnIt() {
        final int result = 42;
        return result;
    }
}
