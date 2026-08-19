/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.plugin.resources;

/**
 * A clean class that lives next to a broken test resource. It is here to
 * prove that qulice still validates real sources, while leaving the Java
 * fixtures in {@code src/test/resources} alone.
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
