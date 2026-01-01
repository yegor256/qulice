/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class FieldInitNoConstructor {
    private final transient int number = 1;

    public int num() {
        return number;
    }
}
