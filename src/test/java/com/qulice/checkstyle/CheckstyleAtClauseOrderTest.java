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
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CheckstyleValidator}'s handling of the
 * stock {@code AtclauseOrder} check.
 * @since 0.25.1
 */
final class CheckstyleAtClauseOrderTest {

    @Test
    void allowsOnlyProperlyOrderedAtClauses() throws Exception {
        final String file = "AtClauseOrder.java";
        final String message = "tags have to appear in the order";
        final String name = "AtclauseOrderCheck";
        MatcherAssert.assertThat(
            "3 tags with wrong order should be found",
            this.runValidation(file, false),
            Matchers.contains(
                new ViolationMatcher(
                    "Javadoc comment at column 3 has parse error.",
                    file,
                    "12",
                    "MissingDeprecatedCheck"
                ),
                new ViolationMatcher(
                    "Javadoc comment at column 3 has parse error.",
                    file,
                    "12",
                    name
                ),
                new ViolationMatcher(
                    message, file, "21", name
                ),
                new ViolationMatcher(
                    message, file, "46", name
                ),
                new ViolationMatcher(
                    "Class Class should be declared as final.",
                    file,
                    "56",
                    "FinalClassCheck"
                )
            )
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
