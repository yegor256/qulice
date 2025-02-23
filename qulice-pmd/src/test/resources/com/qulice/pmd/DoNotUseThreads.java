/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class DoNotUseThreads implements Runnable {
    @Override
    public void run() {
        // do nothing
    }
}
