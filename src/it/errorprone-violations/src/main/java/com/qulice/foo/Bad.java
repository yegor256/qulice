/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.foo;

/**
 * Sample class that contains an ErrorProne SelfAssignment violation.
 * @since 1.0
 */
public final class Bad {

    /**
     * Stored value.
     */
    private int value;

    /**
     * Setter that assigns the field to itself.
     * @param val New value
     */
    public void set(final int val) {
        this.value = this.value;
    }

    /**
     * Getter for the stored value.
     * @return The value
     */
    public int value() {
        return this.value;
    }
}
