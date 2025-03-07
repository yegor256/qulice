/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class FieldInitConstructor {
    private final transient int number;
    private final transient String text = "";

    public FieldInitConstructor() {
        this.number = 2;
    }

    public int num() {
        return number;
    }

    public String tex() {
        return text;
    }
}
