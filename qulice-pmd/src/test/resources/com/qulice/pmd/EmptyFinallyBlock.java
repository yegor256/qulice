/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package emp;

class EmptyFinallyBlock {
    public void bar() {
        try {
            final int x = 1;
            x += 5;
            x++;
        } finally {
            // not good
        }
    }
}
