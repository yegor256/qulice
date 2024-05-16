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
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

/**
 * Common utils and constants for checkstyle tests.
 * @since 0.3
 */
final class Common {

    /**
     * Name of property to set to change location of the license.
     */
    public static final String LICENSE_PROP = "license";

    /**
     * Directory with classes.
     */
    public static final String DIRECTORY = "src/main/java/foo";

    /**
     * License text.
     */
    public static final String LICENSE = "Hello.";

    /**
     * Rule for testing.
     */
    private License rule;

    public void updateRule() {
        this.rule = new License();
    }

    public License getRule() {
        return this.rule;
    }

    /**
     * Validates that checkstyle reported given violation.
     * @param file File to check.
     * @param result Expected validation result.
     * @param message Message to match
     * @throws Exception In case of error
     */
    @SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
    public void validate(final String file, final boolean result,
        final String message) throws Exception {
        MatcherAssert.assertThat(
            this.runValidation(file, result),
            Matchers.hasItem(
                new ViolationMatcher(
                    message, file
                )
            )
        );
    }

    /**
     * Returns string with Checkstyle validation results.
     * @param file File to check.
     * @param passes Whether validation is expected to pass.
     * @return String containing validation results in textual form.
     * @throws IOException In case of error
     */
    @SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
    public Collection<Violation> runValidation(final String file,
        final boolean passes) throws IOException {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.getRule().savePackageInfo(
            new File(mock.basedir(), Common.DIRECTORY)
        ).withLines(Common.LICENSE)
            .withEol("\n").file();
        final Environment env = mock.withParam(
            Common.LICENSE_PROP,
            Common.toUrl(license)
        )
            .withFile(
                String.format("src/main/java/foo/%s", file),
                new IoCheckedText(
                    new TextOf(
                        new ResourceOf(
                            new FormattedText("com/qulice/checkstyle/%s", file)
                        )
                    )
                ).asString()
            );
        final Collection<Violation> results =
            new CheckstyleValidator(env).validate(
                env.files(file)
            );
        if (passes) {
            MatcherAssert.assertThat(
                results,
                Matchers.<Violation>empty()
            );
        } else {
            MatcherAssert.assertThat(
                results,
                Matchers.not(Matchers.<Violation>empty())
            );
        }
        return results;
    }

    /**
     * Convert file name to URL.
     * @param file The file
     * @return The URL
     */
    private static String toUrl(final File file) {
        return String.format("file:%s", file);
    }

}
