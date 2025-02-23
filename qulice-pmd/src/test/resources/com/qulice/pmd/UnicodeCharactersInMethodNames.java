/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class UnderstandsMethodReferences {
    private static final String SOME_STRING = "φ";

    public String φTestMethod() {
        return UnderstandsMethodReferences.SOME_STRING;
    }
}
