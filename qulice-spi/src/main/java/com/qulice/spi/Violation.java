/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Validation result.
 *
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
     * @return Validated file.
     */
    String file();

    /**
     * Lines with the problem.
     * @return Lines with the problem
     */
    String lines();

    /**
     * Validation message.
     * @return Validation message.
     */
    String message();

    /**
     * Default validation result.
     *
     * @since 0.1
     */
    @EqualsAndHashCode
    @ToString
    final class Default implements Violation {

        /**
         * Name of the validator that generated this violation information.
         */
        private final String vldtr;

        /**
         * Name of the failed check.
         */
        private final String name;

        /**
         * Lines with the problem.
         */
        private final String lns;

        /**
         * Validated file.
         */
        private final String file;

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
            this.name = name;
            this.file = file;
            this.lns = lns;
            this.msg = msg;
        }

        @Override
        public String validator() {
            return this.vldtr;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public String file() {
            return this.file;
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
            int cmp = this.vldtr.compareToIgnoreCase(other.validator());
            if (cmp == 0) {
                cmp = this.file.compareTo(other.file());
            }
            if (cmp == 0) {
                // Attempt to parse lines as integers for numeric comparison
                try {
                    final int thisLines = Integer.parseInt(this.lns.split("-")[0]); // Get first line number
                    final int otherLines = Integer.parseInt(other.lines().split("-")[0]);
                    cmp = Integer.compare(thisLines, otherLines);
                } catch (final NumberFormatException ex) {
                    // Fallback to string comparison if parsing fails
                    cmp = this.lns.compareTo(other.lines());
                }
            }
            if (cmp == 0) {
                cmp = this.name.compareToIgnoreCase(other.name());
            }
            if (cmp == 0) {
                cmp = this.msg.compareTo(other.message());
            }
            return cmp;
        }
    }

}
