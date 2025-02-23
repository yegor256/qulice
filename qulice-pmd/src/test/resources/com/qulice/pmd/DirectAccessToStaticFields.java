/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class DirectAccessToStaticFields {
    private static int num = 1;

    public static int number() {
        return num;
    }

    public int another() {
        return 0;
    }
}
