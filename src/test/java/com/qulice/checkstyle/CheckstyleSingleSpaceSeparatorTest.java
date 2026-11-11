/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import java.io.File;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CheckstyleValidator}'s handling of the
 * {@code SingleSpaceSeparatorCheck}.
 *
 * @since 0.24.2
 */
final class CheckstyleSingleSpaceSeparatorTest {

    @Test
    void rejectsDoubleWhitespaceInFieldDeclaration() throws Exception {
        final String file = "DoubleWhitespaceFieldDecl.java";
        final Environment.Mock mock = new Environment.Mock();
        final Environment env = mock.withParam(
            "license",
            String.format(
                "file:%s",
                new License().savePackageInfo(
                    new File(mock.basedir(), "src/main/java/foo")
                ).withLines("Hello.")
                    .withEol(String.valueOf('\n')).file()
            )
        ).withFile(
            String.format("src/main/java/foo/%s", file),
            new IoCheckedText(
                new TextOf(
                    new ResourceOf(
                        new FormattedText("com/qulice/checkstyle/%s", file)
                    )
                )
            ).asString()
        );
        MatcherAssert.assertThat(
            "double whitespace in a field declaration must be reported",
            new CheckstyleValidator(env).validate(env.files(file)),
            Matchers.hasItem(
                new ViolationMatcher(
                    "Use a single space to separate non-whitespace characters.",
                    file, "18", "SingleSpaceSeparatorCheck"
                )
            )
        );
    }
}
