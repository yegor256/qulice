/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class LocalVariableCouldBeFinal {

    public int method() {
        int nonfinal = 0;
        return nonfinal;
    }
}
