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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link CheckstyleValidator}'s handling of the
 * stock {@code MethodName} check.
 * @since 0.28.0
 */
final class CheckstyleMethodNameTest {

    @Test
    void acceptsShortEnglishWords() throws Exception {
        MatcherAssert.assertThat(
            "short English words should be allowed as method names",
            CheckstyleMethodNameTest.violations("ShortMethodNames.java"),
            Matchers.empty()
        );
    }

    @Test
    void rejectsTwoLetterGibberish() throws Exception {
        final String file = "InvalidMethodName.java";
        MatcherAssert.assertThat(
            "two letters that make no word should be reported",
            CheckstyleMethodNameTest.violations(file),
            Matchers.hasItem(
                new ViolationMatcher(
                    "Name 'zz' must match pattern", file, "29", "MethodNameCheck"
                )
            )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"JnaMappedLibrary.java", "JnaDirectBinding.java"})
    void acceptsNativeNamesInJnaBinding(final String file) throws Exception {
        MatcherAssert.assertThat(
            "native names in a JNA binding should not be reported",
            CheckstyleMethodNameTest.violations(file),
            Matchers.not(
                Matchers.hasItem(
                    new ViolationMatcher("", file, "", "MethodNameCheck")
                )
            )
        );
    }

    @Test
    void rejectsNativeNamesOutsideJna() throws Exception {
        final String file = "FakeLibrary.java";
        MatcherAssert.assertThat(
            "a Library that is not JNA's should not be trusted",
            CheckstyleMethodNameTest.violations(file),
            Matchers.hasItem(
                new ViolationMatcher(
                    "Name 'GetLastError' must match pattern",
                    file,
                    "16",
                    "MethodNameCheck"
                )
            )
        );
    }

    @Test
    void obeysSuppressionByShortName() throws Exception {
        MatcherAssert.assertThat(
            "suppressed name should not be reported",
            CheckstyleMethodNameTest.violations("SuppressedMethodName.java"),
            Matchers.empty()
        );
    }

    private static Collection<Violation> violations(final String file)
        throws IOException {
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
        return new CheckstyleValidator(env).validate(env.files(file));
    }
}
