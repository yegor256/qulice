/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class StaticFieldInitConstructor {
    private static final String TEXT = "text";
    private final transient int number;

    public FieldInitConstructor() {
        this.number = 2;
    }

    public int num() {
        return number;
    }

    public String tex() {
        return StaticFieldInitConstructor.TEXT;
    }
}
