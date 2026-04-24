/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class ShortMethodName {
    private final int num;

    public ShortMethodName(final int value) {
        this.num = value;
    }

    public int id() {
        return this.num;
    }
}
