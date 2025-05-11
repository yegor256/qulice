/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.modern;

/**
 * Invalid record example.
 */
// Records must be final
public record InvalidRecord() { // Records must declare at least one component
    private String extraField;  // Records cannot have instance fields
}