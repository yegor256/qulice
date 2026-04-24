/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class StaticInitializerAssignsFinalField {
    private static final String TEXT;

    static {
        TEXT = "";
    }

    public String value() {
        return StaticInitializerAssignsFinalField.TEXT;
    }
}
