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
 * Test case for {@link CheckstyleValidator}'s source-level awareness of the
 * stock {@code UseEnhancedSwitch} check. The check suggests arrow-switch
 * syntax, a Java 14+ feature, so it must be disabled when the project targets
 * an older Java (issue #1694).
 * @since 1.0
 */
final class CheckstyleUseEnhancedSwitchTest {

    /**
     * Name of the resource holding a convertible classic switch.
     */
    private static final String FILE = "EnhancedSwitch.java";

    /**
     * Simple name of the stock check under test.
     */
    private static final String CHECK = "UseEnhancedSwitchCheck";

    @Test
    void skipsEnhancedSwitchOnSourceEight() throws Exception {
        MatcherAssert.assertThat(
            "UseEnhancedSwitch must not fire when source is 8",
            this.runValidation("8"),
            Matchers.not(
                Matchers.hasItem(
                    new ViolationMatcher(
                        "", CheckstyleUseEnhancedSwitchTest.FILE, "",
                        CheckstyleUseEnhancedSwitchTest.CHECK
                    )
                )
            )
        );
    }

    @Test
    void skipsEnhancedSwitchOnLegacySourceEight() throws Exception {
        MatcherAssert.assertThat(
            "UseEnhancedSwitch must not fire when source is 1.8",
            this.runValidation("1.8"),
            Matchers.not(
                Matchers.hasItem(
                    new ViolationMatcher(
                        "", CheckstyleUseEnhancedSwitchTest.FILE, "",
                        CheckstyleUseEnhancedSwitchTest.CHECK
                    )
                )
            )
        );
    }

    @Test
    void skipsEnhancedSwitchWhenSourceUnknown() throws Exception {
        MatcherAssert.assertThat(
            "UseEnhancedSwitch must not fire when source is unknown",
            this.runValidation(null),
            Matchers.not(
                Matchers.hasItem(
                    new ViolationMatcher(
                        "", CheckstyleUseEnhancedSwitchTest.FILE, "",
                        CheckstyleUseEnhancedSwitchTest.CHECK
                    )
                )
            )
        );
    }

    @Test
    void reportsEnhancedSwitchOnModernSource() throws Exception {
        MatcherAssert.assertThat(
            "UseEnhancedSwitch must fire when source is 17",
            this.runValidation("17"),
            Matchers.hasItem(
                new ViolationMatcher(
                    "", CheckstyleUseEnhancedSwitchTest.FILE, "",
                    CheckstyleUseEnhancedSwitchTest.CHECK
                )
            )
        );
    }

    private Collection<Violation> runValidation(final String source)
        throws IOException {
        final Environment.Mock mock = new Environment.Mock();
        mock.withParam(
            "license",
            String.format(
                "file:%s",
                new License().savePackageInfo(
                    new File(mock.basedir(), "src/main/java/foo")
                ).withLines("Hello.")
                    .withEol(String.valueOf('\n')).file()
            )
        );
        if (source != null) {
            mock.withParam("maven.compiler.source", source);
        }
        final Environment env = mock.withFile(
            String.format(
                "src/main/java/foo/%s",
                CheckstyleUseEnhancedSwitchTest.FILE
            ),
            new IoCheckedText(
                new TextOf(
                    new ResourceOf(
                        new FormattedText(
                            "com/qulice/checkstyle/%s",
                            CheckstyleUseEnhancedSwitchTest.FILE
                        )
                    )
                )
            ).asString()
        );
        return new CheckstyleValidator(env).validate(
            env.files(CheckstyleUseEnhancedSwitchTest.FILE)
        );
    }
}
