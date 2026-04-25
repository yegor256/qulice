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
 * stock {@code JavadocPackage} check, in particular the rule
 * relaxing it for {@code src/test/java} when the parallel
 * {@code src/main/java} package already declares
 * {@code package-info.java}. See <a href=
 * "https://github.com/yegor256/qulice/issues/865">#865</a>.
 * @since 0.25.1
 */
final class CheckstyleJavadocPackageTest {

    @Test
    void allowsMissingPackageInfoInTestPackageWhenMainHasIt() throws Exception {
        final String file = "ValidIT.java";
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
            String.format("src/test/java/foo/%s", file),
            new IoCheckedText(
                new TextOf(
                    new ResourceOf(
                        new FormattedText("com/qulice/checkstyle/%s", file)
                    )
                )
            ).asString()
        );
        MatcherAssert.assertThat(
            "JavadocPackage must not fire for src/test/java when src/main/java has package-info.java",
            new CheckstyleValidator(env).validate(env.files(file)),
            Matchers.not(
                Matchers.hasItem(
                    new ViolationMatcher(
                        "Missing package-info.java file", file, "", "JavadocPackageCheck"
                    )
                )
            )
        );
    }

    @Test
    void reportsMissingPackageInfoInTestPackageWhenMainHasNone() throws Exception {
        final String file = "ValidIT.java";
        final Environment.Mock mock = new Environment.Mock();
        final Environment env = mock.withParam(
            "license",
            String.format(
                "file:%s",
                new License().withLines("Hello.")
                    .withEol(String.valueOf('\n')).file()
            )
        ).withFile(
            String.format("src/test/java/foo/%s", file),
            new IoCheckedText(
                new TextOf(
                    new ResourceOf(
                        new FormattedText("com/qulice/checkstyle/%s", file)
                    )
                )
            ).asString()
        );
        MatcherAssert.assertThat(
            "JavadocPackage must fire when neither test nor main package has package-info.java",
            new CheckstyleValidator(env).validate(env.files(file)),
            Matchers.hasItem(
                new ViolationMatcher(
                    "Missing package-info.java file", file, "", "JavadocPackageCheck"
                )
            )
        );
    }
}
