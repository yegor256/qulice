/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class CodeInConstructor {
    private final transient int number;
    private final transient int another;

    public CodeInConstructor() {
        this.number = 2;
        final int a = number + 3;
        this.another = a;
    }

    public int num() {
        return number + another;
    }
}
