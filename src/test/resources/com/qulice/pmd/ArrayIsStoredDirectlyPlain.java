/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class ArrayIsStoredDirectlyPlain {
    private final String[] args;

    public ArrayIsStoredDirectlyPlain(final String... args) {
        this.args = args;
    }

    public String[] list() {
        return this.args.clone();
    }
}
