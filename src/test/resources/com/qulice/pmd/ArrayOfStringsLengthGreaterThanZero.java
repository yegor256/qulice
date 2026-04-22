/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class ArrayOfStringsLengthGreaterThanZero {

    private final String[] strings;

    public ArrayOfStringsLengthGreaterThanZero(final String... args) {
        this.strings = args.clone();
    }

    public String[] args() {
        return this.strings.clone();
    }

    public boolean arrayFromArgs(final String... args) {
        return args.length > 0;
    }

    public boolean arrayFromField() {
        return this.strings.length > 0;
    }

    public boolean arrayFromMethod() {
        return this.args().length > 0;
    }

}
