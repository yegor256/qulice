/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Validates source files via <code>PmdValidator</code>.
 *
 * @since 0.3
 */
@SuppressWarnings("deprecation")
final class SourceValidator {
    /**
     * Rule context.
     */
    private final RuleContext context;

    /**
     * Report listener.
     */
    private final PmdListener listener;

    /**
     * Report renderer (responsible for picking up additional
     * PMD-generated reports with processing errors).
     */
    private final PmdRenderer renderer;

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
        this.context = new RuleContext();
        this.listener = new PmdListener(env);
        this.renderer = new PmdRenderer();
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
        final Collection<DataSource> sources, final String path) {
        this.config.setRuleSets("com/qulice/pmd/ruleset.xml");
        this.config.setThreads(0);
        this.config.setMinimumPriority(RulePriority.LOW);
        this.config.setIgnoreIncrementalAnalysis(true);
        this.config.setShowSuppressedViolations(true);
        this.config.setSourceEncoding(this.encoding.name());
        final Report report = new Report();
        report.addListener(this.listener);
        this.context.setReport(report);
        for (final DataSource source : sources) {
            final String name = source.getNiceFileName(false, path);
            final long start = System.currentTimeMillis();
            Logger.debug(this, "PMD processing file: %s", name);
            this.context.setSourceCodeFile(new File(name));
            this.validateOne(source);
            Logger.debug(
                this,
                "PMD processed file: %[file]s in %[ms]s",
                name,
                System.currentTimeMillis() - start
            );
        }
        this.renderer.exportTo(report);
        report.errors().forEachRemaining(this.listener::onProcessingError);
        report.configErrors().forEachRemaining(this.listener::onConfigError);
        Logger.debug(
            this,
            "got %d errors",
            this.listener.errors().size()
        );
        return this.listener.errors();
    }

    /**
     * Performs validation of one file.
     * @param source Input source file
     */
    private void validateOne(final DataSource source) {
        final net.sourceforge.pmd.RuleSetFactory factory =
            new net.sourceforge.pmd.RuleSetFactory(
                new net.sourceforge.pmd.util.ResourceLoader(),
                RulePriority.LOW,
                false,
                true
            );
        net.sourceforge.pmd.PMD.processFiles(
            this.config,
            factory,
            new LinkedList<>(Collections.singleton(source)),
            this.context,
            Collections.singletonList(this.renderer)
        );
    }
}
