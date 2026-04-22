/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package emp;

class EmptySynchronizedBlock {
    public void bar() {
        synchronized (this) {
            // empty!
        }
    }
}
