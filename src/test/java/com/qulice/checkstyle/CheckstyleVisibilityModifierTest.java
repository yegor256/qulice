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
 * stock {@code VisibilityModifier} check.
 * @since 0.28.0
 */
final class CheckstyleVisibilityModifierTest {

    /**
     * Name of the file with Maven parameters.
     */
    private static final String FILE = "MavenParameters.java";

    /**
     * Name of the check.
     */
    private static final String NAME = "VisibilityModifierCheck";

    @Test
    void acceptsProtectedMavenParameters() throws Exception {
        MatcherAssert.assertThat(
            "protected @Parameter fields should not be reported",
            CheckstyleVisibilityModifierTest.violations(),
            Matchers.not(
                Matchers.hasItems(
                    new ViolationMatcher(
                        "Variable 'dir' must be private and have accessor methods.",
                        CheckstyleVisibilityModifierTest.FILE,
                        "18",
                        CheckstyleVisibilityModifierTest.NAME
                    ),
                    new ViolationMatcher(
                        "Variable 'charset' must be private and have accessor methods.",
                        CheckstyleVisibilityModifierTest.FILE,
                        "24",
                        CheckstyleVisibilityModifierTest.NAME
                    )
                )
            )
        );
    }

    @Test
    void rejectsProtectedFieldsWithoutMavenParameter() throws Exception {
        MatcherAssert.assertThat(
            "protected field without @Parameter should be reported",
            CheckstyleVisibilityModifierTest.violations(),
            Matchers.hasItem(
                new ViolationMatcher(
                    "Variable 'extra' must be private and have accessor methods.",
                    CheckstyleVisibilityModifierTest.FILE,
                    "29",
                    CheckstyleVisibilityModifierTest.NAME
                )
            )
        );
    }

    private static Collection<Violation> violations() throws IOException {
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
            String.format(
                "src/main/java/foo/%s",
                CheckstyleVisibilityModifierTest.FILE
            ),
            new IoCheckedText(
                new TextOf(
                    new ResourceOf(
                        new FormattedText(
                            "com/qulice/checkstyle/%s",
                            CheckstyleVisibilityModifierTest.FILE
                        )
                    )
                )
            ).asString()
        );
        return new CheckstyleValidator(env).validate(
            env.files(CheckstyleVisibilityModifierTest.FILE)
        );
    }
}
