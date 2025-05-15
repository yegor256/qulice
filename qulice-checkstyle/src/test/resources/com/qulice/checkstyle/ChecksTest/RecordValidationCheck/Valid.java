/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.modern;

/**
 * Valid record example.
 *
 * @since 1.0
 */
public final record ValidRecord(String name, int age) {
    /**
     * Constructor.
     * @param name Name
     * @param age Age
     */
    public ValidRecord {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }
} 