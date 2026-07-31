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
 * {@code ParameterNumber} check.
 * @since 1.0
 */
final class CheckstyleParameterNumberTest {

    @Test
    void acceptsConstructorTakingAllAttributes() throws Exception {
        MatcherAssert.assertThat(
            "Constructor with as many parameters as attributes cannot fail",
            this.runValidation("FourAttributes.java", true),
            Matchers.empty()
        );
    }

    @Test
    void rejectsMethodWithTooManyParameters() throws Exception {
        final String file = "FourParameters.java";
        MatcherAssert.assertThat(
            "Method with four parameters cannot pass",
            this.runValidation(file, false),
            Matchers.hasItem(
                new ViolationMatcher(
                    "More than 3 parameters (found 4)",
                    file,
                    "20",
                    "ParameterNumberCheck"
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
