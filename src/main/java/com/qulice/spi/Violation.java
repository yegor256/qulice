/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.util.Comparator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Validation result.
 * @since 0.17
 */
public interface Violation extends Comparable<Violation> {

    /**
     * Name of the validator that generated this violation information.
     * @return Name of the validator
     */
    String validator();

    /**
     * Name of the failed check.
     * @return Name of the failed check
     */
    String name();

    /**
     * Validated file.
     * @return Validated file
     */
    String file();

    /**
     * Lines with the problem.
     * @return Lines with the problem
     */
    String lines();

    /**
     * Validation message.
     * @return Validation message
     */
    String message();

    /**
     * Default validation result.
     * @since 0.1
     */
    @EqualsAndHashCode
    @ToString
    final class Default implements Violation {

        /**
         * Total ordering across all observable fields, so that two
         * violations only tie when they are fully equal.
         */
        private static final Comparator<Violation> ORDER =
            Comparator.comparing(Violation::validator, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Violation::file, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Default::lineNumber)
                .thenComparing(Violation::lines)
                .thenComparing(Violation::name)
                .thenComparing(Violation::message);

        /**
         * Name of the validator that generated this violation information.
         */
        private final String vldtr;

        /**
         * Name of the failed check.
         */
        private final String chk;

        /**
         * Lines with the problem.
         */
        private final String lns;

        /**
         * Validated file.
         */
        private final String path;

        /**
         * Validation message.
         */
        private final String msg;

        /**
         * Constructor.
         * @param vldtr Name of the validator
         * @param name Name of the failed check
         * @param file Validated file
         * @param lns Lines with the problem
         * @param msg Validation message
         * @checkstyle ParameterNumber (3 lines)
         */
        public Default(final String vldtr, final String name,
            final String file, final String lns, final String msg) {
            this.vldtr = vldtr;
            this.chk = name;
            this.path = file;
            this.lns = lns;
            this.msg = msg;
        }

        @Override
        public String validator() {
            return this.vldtr;
        }

        @Override
        public String name() {
            return this.chk;
        }

        @Override
        public String file() {
            return this.path;
        }

        @Override
        public String lines() {
            return this.lns;
        }

        @Override
        public String message() {
            return this.msg;
        }

        @Override
        public int compareTo(final Violation other) {
            return Default.ORDER.compare(this, other);
        }

        /**
         * Numeric line number, or {@link Integer#MAX_VALUE} when the
         * field cannot be parsed as a single integer (e.g. a range like
         * {@code "10-12"}). Numeric ordering keeps line 9 before line 42.
         * @param violation The violation to inspect
         * @return Parsed line number, or {@code MAX_VALUE} as a fallback
         */
        private static int lineNumber(final Violation violation) {
            int parsed;
            try {
                parsed = Integer.parseInt(violation.lines());
            } catch (final NumberFormatException ignored) {
                parsed = Integer.MAX_VALUE;
            }
            return parsed;
        }
    }
}
