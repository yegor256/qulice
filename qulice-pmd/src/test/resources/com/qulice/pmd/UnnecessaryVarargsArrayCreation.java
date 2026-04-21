/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class UnnecessaryVarargsArrayCreation {
    public UnnecessaryVarargsArrayCreation(final String file, final String... lines) {
    }
    public UnnecessaryVarargsArrayCreation(final String... args) {
    }
    public static Object make() {
        return new UnnecessaryVarargsArrayCreation("name", new String[]{"a", "b"});
    }
}
