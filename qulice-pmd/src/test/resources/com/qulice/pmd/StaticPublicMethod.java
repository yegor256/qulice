/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class StaticPublicMethod {

    private FieldInitConstructor() {
        super();
    }

    public static StaticPublicMethod create() {
        return new StaticPublicMethod();
    }

}
