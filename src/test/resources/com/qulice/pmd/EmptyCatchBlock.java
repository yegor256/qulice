/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package emp;

class EmptyCatchBlock {
    public void bar() {
        try {
            final int x = 1;
        } catch (Exception ioe) {
            // not good
        }
    }
}
