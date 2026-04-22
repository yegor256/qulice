/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class UnnecessaryLocalInLoop {

    public void usedInFor() {
        final String cached = "dummy";
        for (int idx = 0; idx < 10; ++idx) {
            System.out.println(cached);
        }
    }

    public void usedInWhile() {
        final String cached = "dummy";
        int idx = 0;
        while (idx < 10) {
            System.out.println(cached);
            ++idx;
        }
    }

    public void usedInDoWhile() {
        final String cached = "dummy";
        int idx = 0;
        do {
            System.out.println(cached);
            ++idx;
        } while (idx < 10);
    }

    public void usedInForeach() {
        final String cached = "dummy";
        for (final Integer idx : java.util.List.of(1, 2, 3)) {
            System.out.println(cached);
        }
    }
}
