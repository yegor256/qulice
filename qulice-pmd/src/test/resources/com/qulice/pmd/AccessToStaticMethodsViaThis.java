/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class AccessToStaticMethodsViaThis {
    private static int number() {
        return 1;
    }

    public int another() {
        return 1 + this.number();
    }
}
