/**
 * Copyright (c) 2011-2013, Qulice.com
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
package com.qulice.codenarc;

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.codenarc.CodeNarcRunner;
import org.codenarc.analyzer.FilesystemSourceAnalyzer;
import org.codenarc.results.Results;
import org.codenarc.rule.Violation;

/**
 * Validates groovy source code with CodeNarc.
 *
 * @todo #148 Create integration tests to check exclusion.
 *  Lets implement integration tests to check that exclusion patterns
 *  propagated to CodeNarc Validator.
 *  For syntax example see
 *  http://www.qulice.com/qulice-maven-plugin/example-exclude.html
 * @author Pavlo Shamrai (pshamrai@gmail.com)
 * @version $Id$
 */
public final class CodeNarcValidator implements Validator {

    @Override
    public void validate(final Environment env) throws ValidationException {
        final File src = new File(env.basedir(), "src");
        if (this.required(src)) {
            final int violations = this.logViolations(
                this.detect(src, env.excludes("codenarc")),
                src
            );
            if (violations > 0) {
                throw new ValidationException(
                    "%d CodeNarc violations (see log above)",
                    violations
                );
            }
        }
    }

    /**
     * Detect all violations.
     * @param src Source code folder
     * @param excludes Exclude patterns with "coma" delimiter
     * @return The result
     */
    private Results detect(final File src, final String excludes) {
        final FilesystemSourceAnalyzer sourceAnalyzer =
            new FilesystemSourceAnalyzer();
        sourceAnalyzer.setBaseDirectory(src.getAbsolutePath());
        sourceAnalyzer.setIncludes("**/*.groovy");
        sourceAnalyzer.setExcludes(excludes);
        final CodeNarcRunner codeNarcRunner = new CodeNarcRunner();
        codeNarcRunner.setSourceAnalyzer(sourceAnalyzer);
        codeNarcRunner.setRuleSetFiles("com/qulice/codenarc/rules.txt");
        codeNarcRunner.setReportWriters(null);
        final Results results = codeNarcRunner.execute();
        Logger.info(
            this,
            "CodeNarc validated %d file(s)",
            results.getTotalNumberOfFiles(true)
        );
        return results;
    }

    /**
     * This folder required CodeNarc validation?
     * @param src Source code folder
     * @return TRUE if there are any groovy files inside
     */
    private boolean required(final File src) {
        boolean required = false;
        if (src.exists()) {
            final int total = FileUtils.listFiles(
                src,
                new String[]{"groovy"},
                true
            ).size();
            if (total == 0) {
                Logger.info(
                    this,
                    "CodeNarc not required since no groovy files in %s",
                    src
                );
            } else {
                required = true;
            }
        } else {
            Logger.info(
                this,
                "CodeNarc not required since no sources in %s",
                src
            );
        }
        return required;
    }

    /**
     * Log all violations.
     * @param list The results from CodeNarc
     * @return Number of found violations
     */
    private int logViolations(final Results list, final File base) {
        int count = 0;
        for (final Object child : list.getChildren()) {
            final Results result = (Results) child;
            if (!result.isFile()) {
                count += this.logViolations(result, base);
                continue;
            }
            for (final Object vltn : result.getViolations()) {
                final Violation violation = (Violation) vltn;
                ++count;
                Logger.error(
                    this,
                    "%s%s%s[%d]: %s (%s)",
                    base.getPath(),
                    File.separator,
                    result.getPath(),
                    violation.getLineNumber(),
                    violation.getMessage(),
                    violation.getRule().getName()
                );
            }
        }
        return count;
    }

}
