/*
 * Copyright (c) 2011-2018, Qulice.com
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
package com.qulice.spotbugs;

import com.jcabi.log.Logger;
import com.jcabi.log.VerboseProcess;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.cactoos.text.JoinedText;
import org.cactoos.text.ReplacedText;
import org.cactoos.text.UncheckedText;

/**
 * Validates source code and compiled binaries with SpotBugs.
 *
 * @since 0.17
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.AvoidDuplicateLiterals"})
public final class SpotBugsValidator implements Validator {

    @Override
    public void validate(final Environment env) throws ValidationException {
        if (env.outdir().exists()) {
            if (!env.exclude("spotbugs", "")) {
                this.check(SpotBugsValidator.spotbugs(env));
            }
        } else {
            Logger.info(
                this,
                "No classes at %s, no SpotBugs validation",
                env.outdir()
            );
        }
    }

    @Override
    public String name() {
        return "FindBugs";
    }

    /**
     * Start spotbugs and return its output.
     * @param env Environment
     * @return Output of spotbugs
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    private static String spotbugs(final Environment env) {
        final List<String> args = new LinkedList<>();
        args.add("java");
        args.addAll(SpotBugsValidator.options());
        args.add(env.basedir().getPath());
        args.add(env.outdir().getPath());
        args.add(
            new UncheckedText(
                new ReplacedText(
                    new JoinedText(
                        ",",
                        env.classpath()
                    ),
                    "\\",
                    "/"
                )
            ).asString()
        );
        return new VerboseProcess(
            new ProcessBuilder(args), Level.INFO, Level.INFO
        ).stdout();
    }

    /**
     * Java options.
     * @return Options
     */
    private static Collection<String> options() {
        return new LinkedList<>();
    }

    /**
     * Check report for errors.
     * @param report The report
     * @throws ValidationException If it contains errors
     */
    private void check(final String report) throws ValidationException {
        int total = 0;
        for (final String line
            : report.split(System.getProperty("line.separator"))) {
            if (line.matches("[a-zA-Z ]+: .*")) {
                Logger.warn(this, "SpotBugs: %s", line);
                ++total;
            }
        }
        if (total > 0) {
            throw new ValidationException(
                "%d SpotBugs violations (see log above)",
                total
            );
        }
    }
}
