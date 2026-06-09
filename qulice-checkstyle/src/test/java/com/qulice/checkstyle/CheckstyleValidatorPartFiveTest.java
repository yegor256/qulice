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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case to check the following files.
 * <br/>ValidLambdaAndGenericsAtEndOfLine.java
 * <br/>InvalidDiamondsUsage.java
 * <br/>ValidDiamondsUsage.java
 * <br/>DiamondUsageNotNeeded.java
 * <br/>ValidLiteralComparisonCheck.java
 *
 * @since 0.3
 */
final class CheckstyleValidatorPartFiveTest {

    /**
     * Test runner.
     */
    private CheckstyleValidateRunner runner;

    @BeforeEach
    void setRule() {
        this.runner = new CheckstyleValidateFileRunner();
    }

    /**
     * CheckstyleValidator can allow final static fields and overrides
     * to have uppercase abbreviations.
     *
     * @throws Exception In case of error
     */
    @Disabled
    @Test
    void checkLambdaAndGenericsAtEndOfLine() throws Exception {
        this.runner.runValidation("ValidLambdaAndGenericsAtEndOfLine.java", true);
    }

    /**
     * CheckstyleValidator can reject non diamond operator usage.
     * @throws Exception If error
     */
    @Test
    void rejectsNonDiamondOperatorUsage() throws Exception {
        final String file = "InvalidDiamondsUsage.java";
        final String name = "DiamondOperatorCheck";
        final String message = "Use diamond operator";
        MatcherAssert.assertThat(
            "Two diamond violations should be found",
            this.runner.runValidation(file, false),
            Matchers.hasItems(
                new ViolationMatcher(message, file, "19", name),
                new ViolationMatcher(message, file, "29", name)
            )
        );
    }

    /**
     * CheckstyleValidator can allow diamond operator usage.
     * @throws Exception If error
     */
    @Test
    void allowsDiamondOperatorUsage() throws Exception {
        this.runner.runValidation("ValidDiamondsUsage.java", true);
    }

    /**
     * CheckstyleValidator allows class name instead of diamond in case
     * of return statement.
     * @throws Exception If error
     */
    @Test
    void allowsFullGenericOperatorUsage() throws Exception {
        this.runner.runValidation("DiamondUsageNotNeeded.java", true);
    }

    /**
     * CheckstyleValidator can allow usage of string literals on either sides.
     * E.g. both {@code txt.equals("contents")}
     * and {@code "contents".equals(txt)} are valid.
     * @throws Exception If error
     */
    @Test
    void allowsStringLiteralsOnBothSideInComparisons()
        throws Exception {
        this.runner.runValidation("ValidLiteralComparisonCheck.java", true);
    }
}
