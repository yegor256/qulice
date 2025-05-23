/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class CallToConstructorInConstructor extends Super {
    private final transient int number;
    private final transient int another;

    public CallToConstructorInConstructor() {
        this(2);
    }

    public CallToConstructorInConstructor(final int a) {
        super(a);
        this.number = 2;
        this.another = a;
    }

    public int num() {
        return number;
    }

    public int getAnother() {
        return another;
    }
}
