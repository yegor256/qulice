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

import com.qulice.checkstyle.test.extensions.CheckstyleValidateFileRunner;
import com.qulice.checkstyle.test.extensions.CheckstyleValidateRunner;
import com.qulice.checkstyle.test.extensions.ViolationMatcher;
import com.qulice.spi.Violation;
import java.util.Collection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case to check the following files.
 * <br/>WindowsEol.java
 * <br/>WindowsEolLinux.java
 * <br/>AnnotationIndentation.java
 * <br/>AnnotationIndentationNegative.java
 * <br/>ExtraSemicolon.java
 * <br/>ValidRecord.java
 * <br/>ValidSemicolon.java
 * <br/>ValidIT.java
 * <br/>ValidITCase.java
 * <br/>CatchParameterNames.java
 * <br/>UrlInLongLine.java
 *
 * @since 0.3
 */
final class CheckstyleValidatorPartThreeTest {

    /**
     * Test runner.
     */
    private CheckstyleValidateRunner runner;

    @BeforeEach
    void setRule() {
        this.runner = new CheckstyleValidateFileRunner();
    }

    /**
     * CheckstyleValidator will fail if  Windows EOL-s are used.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void passesWindowsEndsOfLineWithoutException() throws Exception {
        final String file = "WindowsEol.java";
        final Collection<Violation> results = this.runner.runValidation(file, false);
        MatcherAssert.assertThat(
            "violation should be reported correctly",
            results,
            Matchers.contains(
                new ViolationMatcher(
                    "Line does not match expected header line of ' */'.",
                    file,
                    "3",
                    "HeaderCheck"
                )
            )
        );
    }

    /**
     * Fail validation with Windows-style formatting of the license and
     * Linux-style formatting of the sources.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void testWindowsEndsOfLineWithLinuxSources() throws Exception {
        final String file = "WindowsEolLinux.java";
        final Collection<Violation> results = this.runner.runValidation(file, false);
        MatcherAssert.assertThat(
            "violation should be reported correctly",
            results,
            Matchers.contains(
                new ViolationMatcher(
                    "Line does not match expected header line of ' * Hello.'.",
                    file,
                    "2",
                    "HeaderCheck"
                )
            )
        );
    }

    /**
     * CheckstyleValidator can allow proper indentation in complex annotations.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void allowsProperIndentationInAnnotations() throws Exception {
        this.runner.runValidation("AnnotationIndentation.java", true);
    }

    /**
     * CheckstyleValidator can deny improper indentation in complex annotations.
     * This is regression test for #411.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void rejectsImproperIndentationInAnnotations() throws Exception {
        this.runner.runValidation("AnnotationIndentationNegative.java", false);
    }

    /**
     * Fail validation with extra semicolon in the end
     * of try-with-resources head.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void testExtraSemicolonInTryWithResources() throws Exception {
        this.runner.validate(
            "ExtraSemicolon.java", false,
            "Extra semicolon in the end of try-with-resources head."
        );
    }

    /**
     * Correctly parses Record type.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void testSupportsRecordTypes() throws Exception {
        this.runner.runValidation("ValidRecord.java", true);
    }

    /**
     * Accepts try-with-resources without extra semicolon
     * at the end of the head.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsTryWithResourcesWithoutSemicolon() throws Exception {
        this.runner.runValidation("ValidSemicolon.java", true);
    }

    /**
     * CheckstyleValidator cannot demand methods to be static in files with
     * names ending with {@code ITCase}.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsNonStaticMethodsInIt() throws Exception {
        this.runner.runValidation("ValidIT.java", true);
    }

    /**
     * CheckstyleValidator cannot demand methods to be static in files with
     * names ending with {@code IT}.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsNonStaticMethodsInItCases() throws Exception {
        this.runner.runValidation("ValidITCase.java", true);
    }
}
