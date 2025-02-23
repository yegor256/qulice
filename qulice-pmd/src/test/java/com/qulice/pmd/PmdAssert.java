/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import org.cactoos.text.TextOf;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

/**
 * PMD Validator assertions.
 * @since 0.16
 */
final class PmdAssert {
    /**
     * File to validate.
     */
    private final String file;

    /**
     * Expected build status, true means success.
     */
    private final Matcher<Boolean> result;

    /**
     * Matcher that needs to match.
     */
    private final Matcher<String> matcher;

    /**
     * Constructor.
     * @param file File to validate.
     * @param result Expected build status.
     * @param matcher Matcher that needs to match.
     */
    PmdAssert(final String file, final Matcher<Boolean> result,
        final Matcher<String> matcher) {
        this.file = file;
        this.result = result;
        this.matcher = matcher;
    }

    /**
     * Validates given file against PMD.
     * @throws Exception In case of error.
     */
    public void validate() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/main/java/foo/%s", this.file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(this.file)
            ).asString()
        );
        final Collection<Violation> violations = new PmdValidator(env).validate(
            Collections.singletonList(new File(env.basedir(), name))
        );
        MatcherAssert.assertThat(violations.isEmpty(), this.result);
        final StringBuilder builder = new StringBuilder();
        for (final Violation violation : violations) {
            builder.append(
                String.format(
                    "PMD: %s[%s]: %s (%s)\n",
                    this.file,
                    violation.lines(),
                    violation.message(),
                    violation.name()
                )
            );
        }
        MatcherAssert.assertThat(builder.toString(), this.matcher);
    }
}
