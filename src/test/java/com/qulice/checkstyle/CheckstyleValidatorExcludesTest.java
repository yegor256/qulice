/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CheckstyleValidator} file-exclusion short-circuit.
 *
 * <p>Regression coverage for
 * <a href="https://github.com/yegor256/qulice/issues/759">#759</a>: when
 * every file the validator is asked to check matches an exclude pattern,
 * Checkstyle itself must not be invoked and no violations can be
 * produced.</p>
 *
 * @since 0.24.2
 */
final class CheckstyleValidatorExcludesTest {

    @Test
    void skipsProcessingWhenAllFilesExcluded() throws Exception {
        final String file = "src/main/java/foo/Main.java";
        final Environment env = new ExcludingEnvironment(
            new Environment.Mock().withFile(file, "class Main { }")
        );
        MatcherAssert.assertThat(
            "Excluded files must not yield violations",
            new CheckstyleValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }
}
