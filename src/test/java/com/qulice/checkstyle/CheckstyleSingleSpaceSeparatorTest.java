/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
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
        final File license = new License().savePackageInfo(
            new File(mock.basedir(), "src/main/java/foo")
        ).withLines("Hello.")
            .withEol("\n").file();
        final Environment env = mock.withParam(
            "license",
            String.format("file:%s", license)
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
        final Collection<Violation> results =
            new CheckstyleValidator(env).validate(env.files(file));
        MatcherAssert.assertThat(
            "double whitespace in a field declaration must be reported",
            results,
            Matchers.hasItem(
                new ViolationMatcher(
                    "Use a single space to separate non-whitespace characters.",
                    file, "18", "SingleSpaceSeparatorCheck"
                )
            )
        );
    }
}
