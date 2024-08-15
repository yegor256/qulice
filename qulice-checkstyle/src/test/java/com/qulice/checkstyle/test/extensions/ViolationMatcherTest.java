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
package com.qulice.checkstyle.test.extensions;

import com.qulice.spi.Violation;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

/**
 * Validation results matcher.
 *
 * @since 0.23.1
 */
final class ViolationMatcherTest {

    /**
     * File name.
     */
    private static final String FILE = "file.java";

    /**
     * Number of line.
     */
    private static final String LINE = "5";

    /**
     * Name of the check.
     */
    private static final String CHECK = "check name";

    /**
     * Message.
     */
    private static final String MSG = "message";

    /**
     * Name of the validator.
     */
    private static final String VALDTR = "Validator";

    @Test
    void success() {
        final Violation.Default violation = new Violation.Default(
            ViolationMatcherTest.VALDTR, ViolationMatcherTest.CHECK, ViolationMatcherTest.FILE,
            ViolationMatcherTest.LINE, "this is a long message to check"
        );
        final ViolationMatcher matcher = new ViolationMatcher(
            "message", ViolationMatcherTest.FILE,
            ViolationMatcherTest.LINE, ViolationMatcherTest.CHECK
        );
        MatcherAssert.assertThat(
            "Result should be success.",
            matcher.matchesSafely(violation),
            Matchers.is(true)
        );
    }

    @Test
    void messageSubstringNotFound() {
        final Violation.Default violation = new Violation.Default(
            ViolationMatcherTest.VALDTR, ViolationMatcherTest.CHECK,
            ViolationMatcherTest.LINE, ViolationMatcherTest.LINE, "message"
        );
        final ViolationMatcher matcher = new ViolationMatcher(
            "message1", ViolationMatcherTest.LINE,
            ViolationMatcherTest.LINE, ViolationMatcherTest.CHECK
        );
        MatcherAssert.assertThat(
            "The message does not contain the required substring.",
            matcher.matchesSafely(violation),
            Matchers.is(false)
        );
    }

    @Test
    void endOfFileNameIsNotEqual() {
        final Violation.Default violation = new Violation.Default(
            ViolationMatcherTest.VALDTR, ViolationMatcherTest.CHECK, "file.java1",
            ViolationMatcherTest.LINE, ViolationMatcherTest.MSG
        );
        final ViolationMatcher matcher = new ViolationMatcher(
            ViolationMatcherTest.MSG, "file.java",
            ViolationMatcherTest.LINE, ViolationMatcherTest.CHECK
        );
        MatcherAssert.assertThat(
            "The end of the file name is not equal.",
            matcher.matchesSafely(violation),
            Matchers.is(false)
        );
    }

    @Test
    void lineNotEqual() {
        final Violation.Default violation = new Violation.Default(
            ViolationMatcherTest.VALDTR, ViolationMatcherTest.CHECK,
            ViolationMatcherTest.LINE, "50", ViolationMatcherTest.MSG
        );
        final ViolationMatcher matcher = new ViolationMatcher(
            ViolationMatcherTest.MSG, ViolationMatcherTest.LINE,
            "51", ViolationMatcherTest.CHECK
        );
        MatcherAssert.assertThat(
            "Lines are not equal.",
            matcher.matchesSafely(violation),
            Matchers.is(false)
        );
    }

    @Test
    void emptyCheck() {
        final Violation.Default violation = new Violation.Default(
            ViolationMatcherTest.VALDTR, ViolationMatcherTest.CHECK, ViolationMatcherTest.LINE,
            ViolationMatcherTest.LINE, ViolationMatcherTest.MSG
        );
        final ViolationMatcher matcher = new ViolationMatcher(
            ViolationMatcherTest.MSG, ViolationMatcherTest.LINE,
            ViolationMatcherTest.LINE, ""
        );
        MatcherAssert.assertThat(
            "Empty check should be success.",
            matcher.matchesSafely(violation),
            Matchers.is(true)
        );
    }

    @Test
    void emptyLine() {
        final Violation.Default violation = new Violation.Default(
            ViolationMatcherTest.VALDTR, ViolationMatcherTest.CHECK, ViolationMatcherTest.LINE,
            ViolationMatcherTest.LINE, ViolationMatcherTest.MSG
        );
        final ViolationMatcher matcher = new ViolationMatcher(
            ViolationMatcherTest.MSG, ViolationMatcherTest.LINE,
            "", ViolationMatcherTest.CHECK
        );
        MatcherAssert.assertThat(
            "Empty line should be success.",
            matcher.matchesSafely(violation),
            Matchers.is(true)
        );
    }

    @Test
    void describeTo() {
        final Description description = new StringDescription();
        final ViolationMatcher matcher = new ViolationMatcher(
            "message", ViolationMatcherTest.FILE,
            ViolationMatcherTest.LINE, ViolationMatcherTest.CHECK
        );
        matcher.describeTo(description);
        MatcherAssert.assertThat(
            "Result should be success.",
            description.toString(),
            Matchers.equalTo("doesn't match")
        );
    }
}
