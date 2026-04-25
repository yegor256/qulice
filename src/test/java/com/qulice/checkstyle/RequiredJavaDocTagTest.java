/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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

    @ParameterizedTest
    @MethodSource("params")
    void success(final String[] lines, final String reason, final Matcher<String> expected) {
        final StringBuilder out = new StringBuilder();
        new RequiredJavaDocTag(
            "since",
            Pattern.compile("(?<name>^ +\\* +@since)( +)(?<cont>.*)"),
            Pattern.compile(
                "^\\d+(\\.\\d+){1,2}(\\.[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$"
            ),
            (line, msg, args) -> out.append(MessageFormat.format(msg, args))
        ).matchTagFormat(lines, 0, 2);
        MatcherAssert.assertThat(
            reason,
            out.toString(),
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
            ),
            Arguments.arguments(
                new String[] {" *", " * @since", " *"},
                "Tag present without content should not report as missing",
                Matchers.equalTo(
                    "Malformed '@since' tag, expected ' * @since <value>' format"
                )
            ),
            Arguments.arguments(
                new String[] {" *", " * @since ", " *"},
                "Tag present with empty content should not report as missing",
                Matchers.equalTo(
                    "Malformed '@since' tag, expected ' * @since <value>' format"
                )
            ),
            Arguments.arguments(
                new String[] {" *", " *@since 0.3", " *"},
                "Tag without space after asterisk should not report as missing",
                Matchers.equalTo(
                    "Malformed '@since' tag, expected ' * @since <value>' format"
                )
            )
        );
    }
}
