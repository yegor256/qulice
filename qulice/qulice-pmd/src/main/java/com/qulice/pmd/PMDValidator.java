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
package com.qulice.pmd;

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import com.ymock.util.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.pmd.DataSource;
import net.sourceforge.pmd.FileDataSource;
import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.stat.Metric;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Validates source code with PMD.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (300 lines)
 * @todo #45 Class is too complex now and should be refactored to become
 *  smaller. We should break it down to smaller classes. And we should move
 *  the inner PmdListener class out, to make it a standalone class in the
 *  package. Checkstyle annotation should be removed (see above).
 */
public final class PMDValidator implements Validator {

    /**
     * PMD.
     */
    private final PMD pmd = new PMD();

    /**
     * Rule context.
     */
    private final RuleContext context = new RuleContext();

    /**
     * Rules.
     */
    private final RuleSets ruleSets;

    /**
     * Report listener.
     */
    private final PmdListener reportListener = new PmdListener();

    /**
     * Public ctor.
     */
    public PMDValidator() {
        final RuleSetFactory factory = new RuleSetFactory();
        // @checkstyle MagicNumber (1 line)
        factory.setMinimumPriority(5);
        try {
            this.ruleSets = factory.createRuleSets(
                "com/qulice/pmd/ruleset.xml"
            );
        } catch (RuleSetNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
        final Report report = new Report();
        report.addListener(this.reportListener);
        this.context.setReport(report);
    }

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (3 lines)
     */
    @Override
    public void validate(final Environment env) throws ValidationException {
        final List<DataSource> sources = this.sources(env);
        if (sources.isEmpty()) {
            Logger.info(this, "No files to check with PMD");
            return;
        }
        final File base = env.basedir();
        final String path = base.getPath();
        this.validate(sources, path);
        final List<IRuleViolation> violations =
            this.reportListener.violations();
        if (!violations.isEmpty()) {
            throw new ValidationException(
                "%d PMD violations (see log above)",
                violations.size()
            );
        }
        Logger.info(
            this,
            "No PMD violations found in %d files",
            sources.size()
        );
    }

    /**
     * Performs validation of the input source files.
     * @param sources Input source files.
     * @param path Base path.
     */
    private void validate(
        final Collection<DataSource> sources, final String path) {
        for (DataSource source : sources) {
            final String name = source.getNiceFileName(false, path);
            this.context.setSourceCodeFilename(name);
            this.context.setSourceCodeFile(new File(name));
            this.validateOne(source);
        }
    }

    /**
     * Performs validation of one file.
     * @param source Input source file
     */
    private void validateOne(final DataSource source) {
        InputStream input;
        try {
            input = source.getInputStream();
        } catch (java.io.IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        try {
            final BufferedReader buffer = new BufferedReader(
                new InputStreamReader(input, "UTF8")
            );
            this.pmd.processFile(
                buffer,
                this.ruleSets,
                this.context,
                SourceType.JAVA_16
            );
        } catch (net.sourceforge.pmd.PMDException ex) {
            throw new IllegalArgumentException(ex);
        } catch (java.io.UnsupportedEncodingException ex) {
            throw new IllegalArgumentException(ex);
        } finally {
            try {
                input.close();
            } catch (java.io.IOException ex) {
                Logger.error(this, "Failed to close stream: %s", ex);
            }
        }
    }

    /**
     * Get full list of files to process.
     * @param env The environment.
     * @return List of files
     */
    private List<File> files(final Environment env) {
        final List<File> files = new ArrayList<File>();
        final IOFileFilter filter = new WildcardFileFilter("*.java");
        final File sources = new File(env.basedir(), "");
        if (sources.exists()) {
            files.addAll(
                FileUtils.listFiles(
                    sources,
                    filter,
                    DirectoryFileFilter.INSTANCE
                )
            );
        }
        final File tests = new File(env.basedir(), "src/test/java");
        if (tests.exists()) {
            files.addAll(
                FileUtils.listFiles(
                    tests,
                    filter,
                    DirectoryFileFilter.INSTANCE
                )
            );
        }
        return files;
    }

    /**
     * Get full list of files to process.
     * @param env The environment
     * @return List of sources
     * @see #validate()
     */
    private List<DataSource> sources(final Environment env) {
        final List<DataSource> sources = new ArrayList<DataSource>();
        for (File file : this.files(env)) {
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
            // ignore it
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void ruleViolationAdded(final IRuleViolation violation) {
            this.violations.add(violation);
            Logger.error(
                this,
                "%s[%d-%d]: %s (%s)",
                violation.getFilename(),
                violation.getBeginLine(),
                violation.getEndLine(),
                violation.getDescription(),
                violation.getRule().getName()
            );
        }
    }
}
