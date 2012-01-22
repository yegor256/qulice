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

import com.ymock.util.Logger;
import java.io.File;
import java.io.Reader;
import java.util.Collection;
import net.sourceforge.pmd.DataSource;
import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceType;

/**
 * Validates source files via <code>PMDValidator</code>.
 *
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @version $Id: SourceValidator.java 297 2011-11-13 14:01:00Z guard $
 */
public final class SourceValidator {

    /**
     * PMD.
     */
    private final transient PMD pmd = new PMD();

    /**
     * Rule context.
     */
    private final transient RuleContext context = new RuleContext();

    /**
     * Report listener.
     */
    private final transient PmdListener reportListener = new PmdListener();

    /**
     * Rules.
     */
    private transient RuleSets ruleSets;

    /**
     * Creates new instance of <code>SourceValidator</code>.
     */
    public SourceValidator() {
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
     * Performs validation of the input source files.
     * @param sources Input source files.
     * @param path Base path.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void validate(
        final Collection<DataSource> sources, final String path
    ) {
        for (DataSource source : sources) {
            final String name = source.getNiceFileName(false, path);
            this.context.setSourceCodeFilename(name);
            this.context.setSourceCodeFile(new File(name));
            this.validateOne(source);
        }
    }

    /**
     * Returns violations to take place while validation.
     * @return Collection of violations.
     */
    public Collection<IRuleViolation> getViolations() {
        return this.reportListener.getViolations();
    }

    /**
     * Performs validation of one file.
     * @param source Input source file
     */
    private void validateOne(final DataSource source) {
        final DataSourceReader input = new DataSourceReader(source);
        final Reader reader = input.getReader();
        try {
            this.pmd.processFile(
                reader,
                this.ruleSets,
                this.context,
                SourceType.JAVA_16
            );
        } catch (net.sourceforge.pmd.PMDException ex) {
            throw new IllegalArgumentException(ex);
        } finally {
            try {
                reader.close();
            } catch (java.io.IOException ex) {
                Logger.error(this, "Failed to close stream: %s", ex);
            }
        }
    }
}
