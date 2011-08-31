/**
 * Copyright (c) 2011, Qulice.com
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
package com.qulice.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import net.sourceforge.pmd.DataSource;
import net.sourceforge.pmd.FileDataSource;
import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.stat.Metric;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Validator with PMD.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class PMDValidator extends AbstractValidator {

    /**
     * Public ctor.
     * @param project The project we're working in
     * @param log The Maven log
     * @param config Set of options provided in "configuration" section
     */
    public PMDValidator(final MavenProject project, final Log log,
        final Properties config) {
        super(project, log, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() throws MojoFailureException {
        final List<DataSource> sources = this.sources();
        if (sources.isEmpty()) {
            this.log().info("No files to check with PMD");
            return;
        }
        final RuleSetFactory factory = new RuleSetFactory();
        factory.setMinimumPriority(0);
        final PmdListener listener = new PmdListener();
        final Report report = new Report();
        report.addListener(listener);
        final RuleContext context = new RuleContext();
        context.setReport(report);
        PMD.processFiles(
            // thread count
            1,
            factory,
            SourceType.JAVA_16,
            sources,
            context,
            new ArrayList<Renderer>(),
            // stressTestEnabled
            true,
            "com/qulice/maven/pmd/ruleset.xml",
            // shortNamesEnabled
            true,
            ".",
            "UTF-8",
            "PMD",
            this.getClass().getClassLoader()
        );
        final List<IRuleViolation> violations = listener.violations();
        if (!violations.isEmpty()) {
            throw new MojoFailureException(
                String.format(
                    "%d PMD violations (see log above)",
                    violations.size()
                )
            );
        }
        this.log().info(
            String.format(
                "No PMD violations found in %d files",
                sources.size()
            )
        );
    }

    /**
     * Get full list of files to process.
     * @see #validate()
     */
    private List<DataSource> sources() {
        final List<DataSource> sources = new ArrayList<DataSource>();
        for (File file : this.files()) {
            sources.add(new FileDataSource(file));
        }
        return sources;
    }

    /**
     * Listener of PMD errors.
     */
    private final class PmdListener implements ReportListener {
        /**
         * List of violations.
         */
        private List<IRuleViolation> violations =
            new ArrayList<IRuleViolation>();
        /**
         * Get list of violations.
         * @return List of violations
         */
        public List<IRuleViolation> violations() {
            return this.violations;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void metricAdded(final Metric metric) {
            PMDValidator.this.log().info(
                String.format(
                    "%s: %d %f %f %f %f %f",
                    metric.getMetricName(),
                    metric.getCount(),
                    metric.getTotal(),
                    metric.getLowValue(),
                    metric.getHighValue(),
                    metric.getAverage(),
                    metric.getStandardDeviation()
                )
            );
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void ruleViolationAdded(final IRuleViolation violation) {
            this.violations.add(violation);
            PMDValidator.this.log().info(
                String.format(
                    "%s[%d-%d]: %s (%s)",
                    violation.getFilename(),
                    violation.getBeginLine(),
                    violation.getEndLine(),
                    violation.getDescription(),
                    violation.getRule().getName()
                )
            );
        }
    }

}
