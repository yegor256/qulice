/*
 * Copyright (c) 2011-2024 Qulice.com
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.pmd;

import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleViolation;

/**
 * Represents one PMD error (usually it will be violation).
 *
 * @since 1.0
 */
public interface PmdError {
    /**
     * Returns error name which is short, fixed, human-readable category of
     * the error.
     * @return Error name.
     */
    String name();

    /**
     * Returns file name which caused this error.
     * May return sentinel value if file information is not available.
     * @return File name.
     */
    String fileName();

    /**
     * Returns formatted line range which cause this error.
     * May return sentinel value if line information is not available.
     * @return Formatted line range.
     */
    String lines();

    /**
     * Returns error description.
     * @return Description.
     */
    String description();

    /**
     * PmdError backed by a RuleViolation.
     * @since 1.0
     */
    final class OfRuleViolation implements PmdError {
        /**
         * Internal RuleViolation.
         */
        private final RuleViolation violation;

        /**
         * Creates a new PmdError, representing given RuleViolation.
         * @param violation Internal RuleViolation.
         */
        public OfRuleViolation(final RuleViolation violation) {
            this.violation = violation;
        }

        @Override
        public String name() {
            return this.violation.getRule().getName();
        }

        @Override
        public String fileName() {
            return this.violation.getFilename();
        }

        @Override
        public String lines() {
            return String.format(
                "%d-%d",
                this.violation.getBeginLine(), this.violation.getEndLine()
            );
        }

        @Override
        public String description() {
            return this.violation.getDescription();
        }
    }

    /**
     * PmdError backed by a ProcessingError.
     * @since 1.0
     */
    final class OfProcessingError implements PmdError {
        /**
         * Internal ProcessingError.
         */
        private final ProcessingError error;

        /**
         * Creates a new PmdError, representing given ProcessingError.
         * @param error Internal ProcessingError.
         */
        public OfProcessingError(final ProcessingError error) {
            this.error = error;
        }

        @Override
        public String name() {
            return "ProcessingError";
        }

        @Override
        public String fileName() {
            return this.error.getFile();
        }

        @Override
        public String lines() {
            return "unknown";
        }

        @Override
        public String description() {
            return new StringBuilder()
                .append(this.error.getMsg())
                .append(": ")
                .append(this.error.getDetail())
                .toString();
        }
    }

    /**
     * PmdError backed by a ConfigError.
     * @since 1.0
     */
    final class OfConfigError implements PmdError {
        /**
         * Internal ConfigError.
         */
        private final ConfigurationError error;

        /**
         * Creates a new PmdError, representing given ProcessingError.
         * @param error Internal ProcessingError.
         */
        public OfConfigError(final ConfigurationError error) {
            this.error = error;
        }

        @Override
        public String name() {
            return "ProcessingError";
        }

        @Override
        public String fileName() {
            return "unknown";
        }

        @Override
        public String lines() {
            return "unknown";
        }

        @Override
        public String description() {
            return this.error.issue();
        }
    }
}
