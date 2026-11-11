/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class ExcessiveParameterListInMethod {

    public int total(
        final int one, final int two, final int three, final int four,
        final int five, final int six, final int seven, final int eight,
        final int nine, final int ten
    ) {
        return one + two + three + four + five + six + seven
            + eight + nine + ten;
    }
}
