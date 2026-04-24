/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class AvoidFieldNameMatchingMethodName {
    private final int identifier;

    public AvoidFieldNameMatchingMethodName(final int value) {
        this.identifier = value;
    }

    public int identifier() {
        return this.identifier;
    }
}
