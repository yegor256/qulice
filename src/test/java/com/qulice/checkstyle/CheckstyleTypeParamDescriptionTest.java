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
 * Test case for {@link CheckstyleValidator}'s enforcement that
 * Javadoc {@code @param} type descriptions start with a capital
 * letter, see https://github.com/yegor256/qulice/issues/705.
 * @since 0.25.1
 */
final class CheckstyleTypeParamDescriptionTest {

    @Test
    @SuppressWarnings("unchecked")
    void rejectsLowercaseTypeParamDescription() throws Exception {
        final String file = "InvalidTypeParamDescription.java";
        final String message =
            "@param tag description should start with capital letter";
        final String name = "RegexpSinglelineCheck";
        MatcherAssert.assertThat(
            "Both class and method type parameter descriptions must be reported",
            this.runValidation(file, false),
            Matchers.hasItems(
                new ViolationMatcher(message, file, "8", name),
                new ViolationMatcher(message, file, "15", name)
            )
        );
    }

    @Test
    void allowsUppercaseTypeParamDescription() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidTypeParamDescription.java", true)
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
