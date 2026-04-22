/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class StringLengthGreaterOrEqualZero {

    private final String somestring;

    public StringLengthGreaterOrEqualZero(final String str) {
        this.somestring = str;
    }

    public String someMethod() {
        return this.somestring;
    }

    public boolean lengthOnMethodWithThis() {
        return this.someMethod().length() >= 0;
    }

    public boolean lengthOnMethodWithThisInversed() {
        return 0 <= this.someMethod().length();
    }

    public boolean lengthOnFieldWithThis() {
        return this.somestring.length() >= 0;
    }

    public boolean lengthOnFieldWithThisInversed() {
        return 0 <= this.somestring.length();
    }

    public boolean lengthOnVariable(final String somestring) {
        return somestring.length() >= 0;
    }

}
