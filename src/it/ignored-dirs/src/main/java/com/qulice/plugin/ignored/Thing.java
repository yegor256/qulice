/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.plugin.ignored;

/**
 * A clean class that lives next to the ignored directories. It is here
 * to prove that Qulice still validates real sources, while leaving the
 * Java files in {@code src/site} and {@code src/it} alone.
 * @since 1.0
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
