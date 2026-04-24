/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.plugin.binary;

/**
 * A clean class that lives next to binary resources. Used only to prove
 * that qulice still validates real sources while silently ignoring the
 * binary blobs in {@code src/main/resources}.
 * @since 0.24
 */
public final class Thing {

    /**
     * Name of the thing.
     */
    private final String label;

    /**
     * Ctor.
     * @param name Label to use
     */
    public Thing(final String name) {
        this.label = name;
    }

    /**
     * Print the label.
     * @return Human-readable label
     */
    public String name() {
        return this.label;
    }
}
