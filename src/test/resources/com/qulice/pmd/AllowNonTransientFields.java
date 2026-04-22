/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class AllowNonTransientFields {

    private final int nontransient;

    public AllowNonTransientFields(final int a) {
        this.nontransient = a;
    }

    public int field() {
        return nontransient;
    }

}
