/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package emp;

class EmptySwitchStmt {
    public void bar() {
        final int x = 2;
        switch (x) {
            // empty!
        }
    }
}
