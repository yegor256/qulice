/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CheckstyleValidator}'s handling of the
 * stock {@code LineLength} check and its @checkstyle suppressions.
 * @since 0.25.1
 */
final class CheckstyleLineLengthTest {

    @Test
    @SuppressWarnings("unchecked")
    void reportsErrorWhenCommentOrJavadocIsTooLong() throws Exception {
        MatcherAssert.assertThat(
            "Two long lines should be found",
            this.runValidation("TooLongLines.java", false),
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

    @Test
    void suppressesLineLengthInCommentsViaNearbyComment() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("SuppressLineLengthInComment.java", true)
        );
    }

    @Test
    void doesNotRejectUrlsInLongLines() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("UrlInLongLine.java", true)
        );
    }

    @Test
    void doesNotDoubleCountNonAsciiChars() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("NonAsciiLineLength.java", true)
        );
    }

    private Collection<Violation> runValidation(final String file,
        final boolean passes) throws IOException {
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
        final Collection<Violation> results =
            new CheckstyleValidator(env).validate(env.files(file));
        MatcherAssert.assertThat(
            "validation result should match expected state",
            results.isEmpty(),
            Matchers.is(passes)
        );
        return results;
    }
}
