/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class ExcessiveParameterListSuppressedInConstructor {

    private final int total;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ExcessiveParameterListSuppressedInConstructor(
        final int one, final int two, final int three, final int four,
        final int five, final int six, final int seven, final int eight,
        final int nine, final int ten
    ) {
        this.total = one + two + three + four + five + six + seven
            + eight + nine + ten;
    }

    public int total() {
        return this.total;
    }
}
