/*
 * Copyright (c) 2011-2025 Yegor Bugayenko
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

import java.text.MessageFormat;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link RequiredJavaDocTag} class.
 * @since 0.23.1
 */
final class RequiredJavaDocTagTest {

    /**
     * Logger.
     */
    private final WriterStub writer = new WriterStub();

    /**
     * Object under test.
     */
    private final RequiredJavaDocTag tag = new RequiredJavaDocTag(
        "since",
        Pattern.compile(
            "^\\d+(\\.\\d+){1,2}(\\.[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$"
        ),
        this.writer
    );

    @ParameterizedTest
    @MethodSource("params")
    void success(final String[] lines, final String reason, final Matcher<String> expected) {
        this.tag.matchTagFormat(lines, 0, 2);
        MatcherAssert.assertThat(
            reason,
            this.writer.formattedMessage(),
            expected
        );
    }

    private static Stream<Arguments> params() {
        return Stream.of(
            Arguments.arguments(
                new String[] {" *", " * @since 0.3", " *"},
                "Empty string expected",
                Matchers.emptyString()
            ),
            Arguments.arguments(
                new String[] {" *", "    *    @since    0.3", " *"},
                "Empty string expected",
                Matchers.emptyString()
            ),
            Arguments.arguments(
                new String[] {" *", " * @sinc 0.3", " *"},
                "Expected tag not found",
                Matchers.equalTo("Missing '@since' tag in class/interface comment")
            ),
            Arguments.arguments(
                new String[] {" *", " * @since 0.3.4.4.", " *"},
                "Regular expression non-match expected",
                Matchers.equalTo(
                    String.format(
                        "Tag text '0.3.4.4.' does not match the pattern '%s'",
                        "^\\d+(\\.\\d+){1,2}(\\.[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$"
                    )
                )
            )
        );
    }

    /**
     * Stub for {@link RequiredJavaDocTag.Reporter} class.
     * @since 0.23.1
     */
    private static class WriterStub implements RequiredJavaDocTag.Reporter {

        /**
         * Message.
         */
        private String message;

        /**
         * Ctor.
         */
        WriterStub() {
            this.message = "";
        }

        @Override
        public void log(final int line, final String msg, final Object... args) {
            this.message = MessageFormat.format(msg, args);
        }

        String formattedMessage() {
            return this.message;
        }
    }
}
