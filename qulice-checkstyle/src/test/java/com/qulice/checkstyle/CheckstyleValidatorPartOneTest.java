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
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case to check the following files.
 * <br/>InstanceMethodRef.java
 * <br/>ParametrizedClass.java
 * <br/>LineWrapPackage.java
 * <br/>InvalidIndentation.java
 * <br/>MissingJavadocTest.java
 * <br/>TooLongLines.java
 * <br/>DoNotUseCharEncoding.java
 *
 * @since 0.3
 */
final class CheckstyleValidatorPartOneTest {

    /**
     * Test runner.
     */
    private CheckstyleValidateRunner runner;

    @BeforeEach
    void setRule() {
        this.runner = new CheckstyleValidateFileRunner();
    }

    /**
     * CheckstyleValidator can accept instance method references.
     * @throws Exception In case of error
     */
    @Test
    void acceptsInstanceMethodReferences() throws Exception {
        this.runner.runValidation(
            "InstanceMethodRef.java", true
        );
    }

    /**
     * CheckstyleValidator can report error when parameter object is not
     * documented.
     * @throws Exception In case of error
     */
    @Test
    void reportsErrorWhenParameterObjectIsNotDocumented()
        throws Exception {
        this.runner.validate(
            "ParametrizedClass.java", false,
            "Type Javadoc comment is missing @param <T> tag."
        );
    }

    /**
     * CheckstyleValidator reports an error when package declaration
     * is line wrapped.
     * @throws Exception when error.
     */
    @Test
    void reportsErrorWhenLineWrap()
        throws Exception {
        this.runner.validate(
            "LineWrapPackage.java", false,
            "should not be line-wrapped"
        );
    }

    /**
     * CheckstyleValidator reports an error when indentation is not
     * bigger than previous line by exactly 4.
     * @throws Exception when error.
     */
    @Test
    void reportsErrorWhenIndentationIsIncorrect() throws Exception {
        this.runner.validate(
            "InvalidIndentation.java",
            false,
            "Indentation (14) must be same or less than"
        );
    }

    /**
     * CheckstyleValidator does not report an error when there is no JavaDoc
     * on method in JUnit tests.
     * @throws Exception when error.
     */
    @Test
    void doesNotReportErrorWhenMissingJavadocInTests() throws Exception {
        this.runner.runValidation("MissingJavadocTest.java", true);
    }

    /**
     * CheckstyleValidator reports an error when comment or Javadoc has too
     * long line.
     * @throws Exception when error.
     */
    @Test
    @SuppressWarnings("unchecked")
    void reportsErrorWhenCommentOrJavadocIsTooLong() throws Exception {
        final Collection<Violation> results =
            this.runner.runValidation("TooLongLines.java", false);
        MatcherAssert.assertThat(
            "Two long lines should be found",
            results,
            Matchers.hasItems(
                new ViolationMatcher(
                    "Line is longer than 100 characters (found 104)", ""
                ),
                new ViolationMatcher(
                    "Line is longer than 100 characters (found 103)", ""
                )
            )
        );
    }

    /**
     * CheckstyleValidator can report Apache Commons {@code CharEncoding} class
     * usages.
     * @throws Exception when error.
     */
    @Test
    @SuppressWarnings("unchecked")
    void reportsAllCharEncodingUsages() throws Exception {
        final String message =
            "Use java.nio.charset.StandardCharsets instead";
        final String file = "DoNotUseCharEncoding.java";
        final Collection<Violation> results = this.runner.runValidation(
            file, false
        );
        final String name = "RegexpSinglelineCheck";
        MatcherAssert.assertThat(
            "8 violations should be found",
            results,
            new IsIterableContainingInOrder<>(
                new ListOf<>(
                    new ViolationMatcher(
                        message, file, "6", name
                    ),
                    new ViolationMatcher(
                        message, file, "7", name
                    ),
                    new ViolationMatcher(
                        message, file, "8", name
                    ),
                    new ViolationMatcher(
                        message, file, "9", name
                    ),
                    new ViolationMatcher(
                        message, file, "22", name
                    ),
                    new ViolationMatcher(
                        message, file, "23", name
                    ),
                    new ViolationMatcher(
                        message, file, "24", name
                    ),
                    new ViolationMatcher(
                        message, file, "25", name
                    )
                )
            )
        );
    }
}
