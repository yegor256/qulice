/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public class UselessParentheses {
    private int bar1;
    private Integer bar2;

    public void setBar(int n) {
        bar1 = Integer.valueOf((n));
        bar2 = (n);
    }

    public int sum() {
        return bar1 + bar2;
    }
}
