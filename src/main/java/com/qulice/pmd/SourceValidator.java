/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.rule.RulePriority;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;
import org.cactoos.list.ListOf;

/**
 * Validates source files via <code>PmdValidator</code>.
 *
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
     * @param env Environment
     */
    SourceValidator(final Environment env) {
        this.config = new PMDConfiguration();
        this.encoding = env.encoding();
    }

    /**
     * Performs validation of the input source files.
     * @param sources Input source files
     * @param path Base path
     * @return Collection of violations
     */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.CloseResource"})
    Collection<PmdError> validate(
        final Collection<File> sources, final String path) {
        this.config.setRuleSets(new ListOf<>("com/qulice/pmd/ruleset.xml"));
        this.config.setThreads(0);
        this.config.setMinimumPriority(RulePriority.LOW);
        this.config.setIgnoreIncrementalAnalysis(true);
        this.config.setShowSuppressedViolations(true);
        this.config.setSourceEncoding(this.encoding);
        final List<PmdError> errors = new LinkedList<>();
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
                .map(PmdError.OfProcessingError::new).forEach(errors::add);
            report.getViolations().stream()
                .filter(violation -> !SourceValidator.suppressesItself(violation))
                .map(PmdError.OfRuleViolation::new)
                .forEach(errors::add);
        }
        return errors;
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
