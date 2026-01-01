/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.rule.RulePriority;
import net.sourceforge.pmd.reporting.Report;
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
     * @param sources Input source files.
     * @param path Base path.
     * @return Collection of violations.
     */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.CloseResource"})
    public Collection<PmdError> validate(
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
            report.getViolations().stream().map(PmdError.OfRuleViolation::new)
                .forEach(errors::add);
        }
        return errors;
    }
}
