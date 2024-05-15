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
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for file metainfo validation.
 * @since 0.3
 */
@SuppressWarnings(
    {
        "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals", "PMD.GodClass"
    }
)
final class ChecksMetainfoTest {

    /**
     * Name of property to set to change location of the license.
     */
    private static final String LICENSE_PROP = "license";

    /**
     * Directory with classes.
     */
    private static final String DIRECTORY = "src/main/java/foo";

    /**
     * License text.
     */
    private static final String LICENSE = "Hello.";

    /**
     * Rule for testing.
     */
    private License rule;

    @BeforeEach
    public void setRule() {
        this.rule = new License();
    }

    /**
     * CheckstyleValidator can deny binary contents in java file.
     * This is test for #1264.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void rejectsJavaFileWithBinaryContent() throws Exception {
        this.runValidation("JavaFileWithBinaryContent.java", false);
    }

    /**
     * Returns string with Checkstyle validation results.
     * @param file File to check.
     * @param passes Whether validation is expected to pass.
     * @return String containing validation results in textual form.
     * @throws IOException In case of error
     */
    @SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
    private Collection<Violation> runValidation(final String file,
        final boolean passes) throws IOException {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule.savePackageInfo(
            new File(mock.basedir(), ChecksMetainfoTest.DIRECTORY)
        ).withLines(ChecksMetainfoTest.LICENSE)
            .withEol("\n").file();
        final Environment env = mock.withParam(
            ChecksMetainfoTest.LICENSE_PROP,
            this.toUrl(license)
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
    private String toUrl(final File file) {
        return String.format("file:%s", file);
    }

    /**
     * Validation results matcher.
     *
     * @since 0.1
     */
    private static final class ViolationMatcher extends
        TypeSafeMatcher<Violation> {

        /**
         * Message to check.
         */
        private final String message;

        /**
         * File to check.
         */
        private final String file;

        /**
         * Expected line.
         */
        private final String line;

        /**
         * Check name.
         */
        private final String check;

        /**
         * Constructor.
         * @param message Message to check
         * @param file File to check
         * @param line Line to check
         * @param check Check name
         * @checkstyle ParameterNumber (3 lines)
         */
        ViolationMatcher(final String message, final String file,
            final String line, final String check) {
            super();
            this.message = message;
            this.file = file;
            this.line = line;
            this.check = check;
        }

        /**
         * Constructor.
         * @param message Message to check
         * @param file File to check
         */
        ViolationMatcher(final String message, final String file) {
            this(message, file, "", "");
        }

        @Override
        public boolean matchesSafely(final Violation item) {
            return item.message().contains(this.message)
                && item.file().endsWith(this.file)
                && this.lineMatches(item)
                && this.checkMatches(item);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("doesn't match");
        }

        /**
         * Check name matches.
         * @param item Item to check.
         * @return True if check name matches.
         */
        private boolean checkMatches(final Violation item) {
            return this.check.isEmpty()
                || !this.check.isEmpty() && item.name().equals(this.check);
        }

        /**
         * Check that given line matches.
         * @param item Item to check.
         * @return True if line matches.
         */
        private boolean lineMatches(final Violation item) {
            return this.line.isEmpty()
                || !this.line.isEmpty() && item.lines().equals(this.line);
        }
    }
}
