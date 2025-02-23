/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.foo;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sample class.
 * @since 1.0
 */
public final class Violations {
    /**
     * Unused variable - PMD should report a violation here.
     */
    private Integer var;

    /**
     * Calculate square of a number.
     * @param num The number
     * @return The square
     * @checkstyle NonStaticMethod (2 lines)
     */
    public int square(final int num) {
        return num * num;
    }

    /**
     * Returns Foo.
     * @return Foo.
     * @checkstyle NonStaticMethod (2 lines)
     */
    public Foo doSmth() {
        final String name = "test".toUpperCase();
        return new Foo(name);
    }

    /**
     * Returns Foo again.
     * @return Foo.
     * @checkstyle NonStaticMethod (2 lines)
     */
    public Foo doSmthElse() {
        String name = "other";
        name = String.format("%s append", name);
        return new Foo(name);
    }

    /**
     * Prints something.
     * @checkstyle NonStaticMethod (2 lines)
     */
    public void print() {
        final String message = "hello";
        System.out.println(message);
    }

    /**
     * Test class.
     */
    private static final class Foo {
        /**
         * Name.
         */
        private final transient String name;

        /**
         * Constructor.
         * @param nam Name.
         */
        Foo(final String nam) {
            this.name = nam;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public String something() {
            return Stream.of(" one", " two")
                .map(str -> str.trim())
                .collect(Collectors.joining());

        }
    }
}
