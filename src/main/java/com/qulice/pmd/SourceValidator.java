/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.rule.RulePriority;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;
import org.cactoos.list.ListOf;

/**
 * Validates source files via <code>PmdValidator</code>.
 * @since 0.3
 */
final class SourceValidator {

    /**
     * Rules.
     */
    private final PMDConfiguration config;

    /**
     * Source files encoding.
     */
    private final Charset encoding;

    /**
     * Creates new instance of <code>SourceValidator</code>.
     * @param charset Source files encoding
     */
    SourceValidator(final Charset charset) {
        this.config = new PMDConfiguration();
        this.encoding = charset;
    }

    /**
     * Performs validation of the input source files.
     * @param sources Input source files
     * @param path Base path
     * @return Collection of violations
     */
    Collection<PmdError> validate(
        final Collection<File> sources, final String path) {
        this.config.setRuleSets(new ListOf<>("com/qulice/pmd/ruleset.xml"));
        this.config.setThreads(0);
        this.config.setMinimumPriority(RulePriority.LOW);
        this.config.setIgnoreIncrementalAnalysis(true);
        this.config.setShowSuppressedViolations(true);
        this.config.setSourceEncoding(this.encoding);
        final List<PmdError> errors = new ArrayList<>(0);
        try (PmdAnalysis analysis = PmdAnalysis.create(this.config)) {
            for (final File source : sources) {
                Logger.debug(
                    this,
                    "Processing file: %s",
                    source.toPath().toString()
                );
                analysis.files().addFile(source.toPath());
            }
            final Report report = analysis.performAnalysisAndCollectReport();
            report.getConfigurationErrors().stream()
                .map(PmdError.OfConfigError::new).forEach(errors::add);
            report.getProcessingErrors().stream()
                .filter(this::reportable)
                .map(PmdError.OfProcessingError::new).forEach(errors::add);
            report.getViolations().stream()
                .filter(violation -> !SourceValidator.suppressesItself(violation))
                .map(PmdError.OfRuleViolation::new)
                .forEach(errors::add);
        }
        return errors;
    }

    /**
     * Tells whether a processing error should be reported as a violation.
     * Some PMD rules crash internally with an {@link IllegalStateException}
     * while analyzing perfectly valid Java. For example {@code
     * UseDiamondOperator} throws {@code "overload resolution is not
     * complete"} when it probes an overloaded method reference such as
     * {@code BigDecimal::multiply} (see #1686). PMD itself only logs such a
     * crash as a warning and keeps going, so it is a bug in the tool, not a
     * problem in the code under analysis. We do the same here: log it as a
     * warning and do not turn it into a build-breaking violation, since the
     * user cannot fix it in their code. The full stack trace is logged too,
     * so the crash can still be diagnosed and reported upstream.
     * @param error The processing error to inspect
     * @return True if it must be reported, false if it is an internal crash
     */
    private boolean reportable(final Report.ProcessingError error) {
        final boolean crash = SourceValidator.crashed(error.getError());
        if (crash) {
            Logger.warn(
                this,
                "PMD rule crashed on %s and was ignored: %s%n%s",
                error.getFileId().getAbsolutePath(),
                error.getMsg(),
                error.getDetail()
            );
        }
        return !crash;
    }

    /**
     * Tells whether a throwable is (or was caused by) an internal PMD rule
     * crash, recognized by an {@link IllegalStateException} anywhere in its
     * cause chain.
     * @param error The throwable to inspect
     * @return True if the cause chain contains an IllegalStateException
     */
    private static boolean crashed(final Throwable error) {
        boolean crash = false;
        Throwable cause = error;
        while (cause != null) {
            if (cause instanceof IllegalStateException) {
                crash = true;
                break;
            }
            cause = cause.getCause();
        }
        return crash;
    }

    /**
     * Tells whether a violation reports a {@code @SuppressWarnings} that
     * tries to suppress {@code PMD.UnnecessaryWarningSuppression} itself.
     * The PMD rule cannot suppress its own violations, so suppressing it is
     * effectively a no-op and must not be reported as unused.
     * @param violation Violation to inspect
     * @return True if the violation is self-referential
     */
    private static boolean suppressesItself(final RuleViolation violation) {
        final String name = "UnnecessaryWarningSuppression";
        boolean result = false;
        if (name.equals(violation.getRule().getName())) {
            try {
                final List<String> lines = Files.readAllLines(
                    Paths.get(violation.getFileId().getAbsolutePath())
                );
                final int start = Math.max(0, violation.getBeginLine() - 1);
                final int end = Math.min(lines.size(), violation.getEndLine());
                for (int idx = start; idx < end; ++idx) {
                    if (lines.get(idx).contains(name)) {
                        result = true;
                        break;
                    }
                }
            } catch (final IOException ex) {
                Logger.debug(
                    SourceValidator.class,
                    "Failed to read %s: %s",
                    violation.getFileId().getAbsolutePath(),
                    ex.getMessage()
                );
            }
        }
        return result;
    }
}
