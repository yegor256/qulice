/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package emp;

class EmptyStatementBlock {
    private int baz;

    public void setBar(int bar) {
        { baz = bar; } // Why not?
        {} // But remove this.
    }
}
