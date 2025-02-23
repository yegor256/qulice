/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.spi.Environment;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleViolation;

/**
 * Listener of PMD errors.
 *
 * @since 0.3
 */
@SuppressWarnings("deprecation")
final class PmdListener implements net.sourceforge.pmd.ThreadSafeReportListener {

    /**
     * Environment.
     */
    private final Environment env;

    /**
     * All errors spotted (mostly violations, but also processing
     * and config errors).
     */
    private final Collection<PmdError> violations;

    /**
     * Public ctor.
     * @param environ Environment
     */
    PmdListener(final Environment environ) {
        this.violations = new LinkedList<>();
        this.env = environ;
    }

    @Override
    public void metricAdded(final net.sourceforge.pmd.stat.Metric metric) {
        // ignore it
    }

    @Override
    public void ruleViolationAdded(final RuleViolation violation) {
        final String name = violation.getFilename().substring(
            this.env.basedir().toString().length()
        );
        if (!this.env.exclude("pmd", name)) {
            this.violations.add(new PmdError.OfRuleViolation(violation));
        }
    }

    /**
     * Registers a new ProcessingError.
     * @param error A processing error that needs to be reported.
     * @todo #1129 If was added to avoid failing build, but there should be
     *  better place for this check.
     */
    public void onProcessingError(final ProcessingError error) {
        if (error.getFile().endsWith(".java")) {
            this.violations.add(new PmdError.OfProcessingError(error));
        }
    }

    /**
     * Registers a new ConfigurationError.
     * @param error A configuration error that needs to be reported.
     */
    public void onConfigError(final ConfigurationError error) {
        this.violations.add(new PmdError.OfConfigError(error));
    }

    /**
     * Get list of violations.
     * @return List of violations
     */
    public Collection<PmdError> errors() {
        return Collections.unmodifiableCollection(this.violations);
    }
}
