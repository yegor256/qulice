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
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case to check the following files.
 * <br/>CatchParameterNames.java
 * <br/>UrlInLongLine.java
 * <br/>BlankLinesOutsideMethodsPass.java
 * <br/>BlankLinesInsideMethodsFail.java
 * <br/>InvalidAbbreviationAsWordInNameXML.java
 * <br/>HiddenParameter.java
 * <br/>ValidAbbreviationAsWordInNameIT.java
 * <br/>ValidAbbreviationAsWordInName.java
 *
 * @since 0.3
 */
final class CheckstyleValidatorPartFourTest {

    /**
     * Test runner.
     */
    private CheckstyleValidateRunner runner;

    @BeforeEach
    void setRule() {
        this.runner = new CheckstyleValidateFileRunner();
    }

    /**
     * CheckstyleValidator can distinguish between valid and invalid
     * catch parameter names.
     * @throws Exception In case of error
     */
    @Test
    @SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals"})
    void distinguishesValidCatchParameterNames() throws Exception {
        final String file = "CatchParameterNames.java";
        final Collection<Violation> results = this.runner.runValidation(
            file, false
        );
        MatcherAssert.assertThat(
            "Should be 3 violations",
            results,
            Matchers.hasSize(3)
        );
        final String name = "CatchParameterNameCheck";
        MatcherAssert.assertThat(
            "All naming violations should be found",
            results,
            Matchers.hasItems(
                new ViolationMatcher(
                    "Name 'ex_invalid_1' must match pattern", file, "26", name
                ),
                new ViolationMatcher(
                    "Name '$xxx' must match pattern", file, "28", name
                ),
                new ViolationMatcher(
                    "Name '_exp' must match pattern", file, "30", name
                )
            )
        );
    }

    /**
     * Test if URLs are valid despite having a line length over 80.
     * @throws Exception In case of error
     */
    @Test
    void doesNotRejectUrlsInLongLines() throws Exception {
        this.runner.runValidation("UrlInLongLine.java", true);
    }

    /**
     * CheckstyleValidator can allow spaces between methods of anonymous
     * classes.
     * @throws Exception In case of error
     */
    @Test
    void allowsSpacesBetweenMethodsOfAnonymousClasses()
        throws Exception {
        this.runner.runValidation("BlankLinesOutsideMethodsPass.java", true);
    }

    /**
     * CheckstyleValidator can reject spaces inside methods, regardless of
     * whether they are inside of an anonymous method or not.
     * @throws Exception In case of error
     */
    @Test
    @SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals"})
    void rejectsSpacesInsideMethods() throws Exception {
        final String file = "BlankLinesInsideMethodsFail.java";
        final Collection<Violation> result = this.runner.runValidation(
            file, false
        );
        final String name = "EmptyLinesCheck";
        final String message = "Empty line inside method";
        MatcherAssert.assertThat(
            "All empty lines should be found",
            result,
            Matchers.hasItems(
                new ViolationMatcher(message, file, "15", name),
                new ViolationMatcher(message, file, "19", name),
                new ViolationMatcher(message, file, "21", name),
                new ViolationMatcher(message, file, "25", name),
                new ViolationMatcher(message, file, "28", name),
                new ViolationMatcher(message, file, "32", name),
                new ViolationMatcher(message, file, "34", name),
                new ViolationMatcher(message, file, "38", name),
                new ViolationMatcher(message, file, "41", name),
                new ViolationMatcher(message, file, "48", name),
                new ViolationMatcher(message, file, "50", name),
                new ViolationMatcher(message, file, "52", name)
            )
        );
    }

    /**
     * CheckstyleValidator can reject uppercase abbreviations in naming
     * outside of final static fields.
     *
     * @throws Exception In case of error
     */
    @Test
    @SuppressWarnings("unchecked")
    void rejectsUppercaseAbbreviations() throws Exception {
        final String file = "InvalidAbbreviationAsWordInNameXML.java";
        final Collection<Violation> results = this.runner.runValidation(
            file, false
        );
        final String name = "AbbreviationAsWordInNameCheck";
        final String message = new Joined(
            " ",
            "Abbreviation in name '%s'",
            "must contain no more than '2' consecutive capital letters."
        ).asString();
        MatcherAssert.assertThat(
            "All long abbreviations should be found",
            results,
            Matchers.hasItems(
                new ViolationMatcher(
                    String.format(
                        message, "InvalidAbbreviationAsWordInNameXML"
                    ),
                    file, "11", name
                ),
                new ViolationMatcher(
                    String.format(message, "InvalidHTML"), file,
                    "15", name
                )
            )
        );
    }

    @Test
    void rejectsHiddenParameters() throws Exception {
        final String file = "HiddenParameter.java";
        final Collection<Violation> results = this.runner.runValidation(
            file, false
        );
        final String name = "HiddenFieldCheck";
        final String message = "'test' hides a field.";
        MatcherAssert.assertThat(
            "Hidden parameter in methods should be found",
            results,
            Matchers.hasItems(
                new ViolationMatcher(
                    message, file, "17", name
                )
            )
        );
    }

    /**
     * CheckstyleValidator can allow IT as an uppercase abbreviation.
     *
     * @throws Exception In case of error
     */
    @Test
    void allowsITUppercaseAbbreviation() throws Exception {
        this.runner.runValidation("ValidAbbreviationAsWordInNameIT.java", true);
    }

    /**
     * CheckstyleValidator can allow final static fields and overrides
     * to have uppercase abbreviations.
     *
     * @throws Exception In case of error
     */
    @Test
    void allowsUppercaseAbbreviationExceptions() throws Exception {
        this.runner.runValidation("ValidAbbreviationAsWordInName.java", true);
    }
}
