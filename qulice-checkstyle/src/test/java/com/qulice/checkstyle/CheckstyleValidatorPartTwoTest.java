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
 * <br/>ValidSingleLineCommentCheck.java
 * <br/>ValidIndentation.java
 * <br/>ReturnCount.java
 * <br/>DefaultMethods.java
 * <br/>AnnotationConstant.java
 * <br/>ConstructorParams.java
 * <br/>LocalVariableNames.java
 * <br/>AtClauseOrder.java
 *
 * @since 0.3
 */
final class CheckstyleValidatorPartTwoTest {

    /**
     * Test runner.
     */
    private CheckstyleValidateRunner runner;

    @BeforeEach
    void setRule() {
        this.runner = new CheckstyleValidateFileRunner();
    }

    /**
     * CheckstyleValidator accepts string literal which
     * contains multiline comment.
     * @throws Exception If test failed.
     */
    @Test
    void acceptsValidSingleLineComment() throws Exception {
        this.runner.runValidation(
            "ValidSingleLineCommentCheck.java", true
        );
    }

    /**
     * CheckstyleValidator accepts the valid indentation
     * refused by forceStrictCondition.
     * @throws Exception when error.
     */
    @Test
    void acceptsValidIndentation() throws Exception {
        this.runner.runValidation(
            "ValidIndentation.java", true
        );
    }

    /**
     * CheckstyleValidator reports an error when any method contains more
     * than one return statement.
     * @throws Exception when error.
     */
    @Test
    void reportsErrorOnMoreThanOneReturnStatement() throws Exception {
        this.runner.validate(
            "ReturnCount.java", false,
            "Return count is 2 (max allowed for non-void methods/lambdas is 1)"
        );
    }

    /**
     * CheckstyleValidator can accept default methods with final modifiers.
     * @throws Exception In case of error
     */
    @Test
    void acceptsDefaultMethodsWithFinalModifiers() throws Exception {
        this.runner.runValidation(
            "DefaultMethods.java", true
        );
    }

    /**
     * CheckstyleValidator can accept constant used in method annotation.
     * @throws Exception In case of error
     * @todo #447:30min Right now ConstantUsageCheck takes into account
     *  usage of constants inside method annotations, add handling of constants
     *  used in field and class annotations.
     */
    @Test
    void acceptsConstantUsedInMethodAnnotation() throws Exception {
        this.runner.runValidation("AnnotationConstant.java", true);
    }

    /**
     * CheckstyleValidator can allow constructor parameters named just like
     * fields.
     * @throws Exception In case of error
     */
    @Test
    void acceptsConstructorParametersNamedJustLikeFields()
        throws Exception {
        final String file = "ConstructorParams.java";
        final Collection<Violation> results = this.runner.runValidation(file, false);
        final String name = "HiddenFieldCheck";
        MatcherAssert.assertThat(
            "Two hidden fields in ctor should be found",
            results,
            Matchers.allOf(
                Matchers.hasItem(
                    new ViolationMatcher(
                        "'number' hides a field.", file, "29", name
                    )
                ),
                Matchers.not(
                    Matchers.hasItem(
                        new ViolationMatcher(
                            "'number' hides a field.", file, "20", name
                        )
                    )
                )
            )
        );
    }

    /**
     * CheckstyleValidator allows local variables and catch parameters with
     * names matching {@code ^[a-z]{3,12}$} pattern.
     * Additionally, catch parameters can use name {@code ex}.
     * @throws Exception In case of error
     */
    @Test
    @SuppressWarnings("unchecked")
    void allowsOnlyProperlyNamedLocalVariables() throws Exception {
        final String file = "LocalVariableNames.java";
        final Collection<Violation> results = this.runner.runValidation(
            file, false
        );
        MatcherAssert.assertThat(
            "10 total violations should be found",
            results,
            Matchers.hasSize(10)
        );
        MatcherAssert.assertThat(
            "Only invalid variables name should be found",
            results,
            Matchers.allOf(
                Matchers.not(
                    Matchers.hasItems(
                        new ViolationMatcher("aaa", file),
                        new ViolationMatcher("twelveletter", file),
                        new ViolationMatcher("ise", file),
                        new ViolationMatcher("id", file),
                        new ViolationMatcher("parametername", file)
                    )
                ),
                Matchers.hasItems(
                    new ViolationMatcher(
                        "Name 'prolongations' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'very_long_variable_id' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'camelCase' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'it' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'number1' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'ex' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'a' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'ae' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'e' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'it' must match pattern", file
                    )
                )
            )
        );
    }

    /**
     * CheckstyleValidator can allow only properly ordered Javadoc at-clauses.
     * @throws Exception In case of error
     */
    @Test
    void allowsOnlyProperlyOrderedAtClauses() throws Exception {
        final String file = "AtClauseOrder.java";
        final Collection<Violation> results = this.runner.runValidation(
            file, false
        );
        final String message = "tags have to appear in the order";
        final String name = "AtclauseOrderCheck";
        MatcherAssert.assertThat(
            "3 tags with wrong order should be found",
            results,
            Matchers.contains(
                new ViolationMatcher(
                    message, file, "14", name
                ),
                new ViolationMatcher(
                    message, file, "21", name
                ),
                new ViolationMatcher(
                    message, file, "48", name
                ),
                new ViolationMatcher(
                    "Class Class should be declared as final.", file, "59", "FinalClassCheck"
                )
            )
        );
    }
}
