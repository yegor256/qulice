/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class AccessToStaticFieldsViaThis {
    private static final int num = 1;

    public int number() {
        return this.num;
    }
}
