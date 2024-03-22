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

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import java.io.File;
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
     * Creates new instance of <code>SourceValidator</code>.
     * @param env Environment
     */
    SourceValidator(final Environment env) {
        this.context = new RuleContext();
        this.listener = new PmdListener(env);
        this.renderer = new PmdRenderer();
        this.config = new PMDConfiguration();
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
        final Report report = new Report();
        report.addListener(this.listener);
        this.context.setReport(report);
        for (final DataSource source : sources) {
            final String name = source.getNiceFileName(false, path);
            Logger.debug(this, "Processing file: %s", name);
            this.context.setSourceCodeFile(new File(name));
            this.validateOne(source);
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
