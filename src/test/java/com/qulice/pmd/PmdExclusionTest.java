/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s short-circuit when every
 * source file is excluded (issue #759). PMD must not be invoked
 * on the excluded file and must report no violations.
 * @since 0.25.1
 */
final class PmdExclusionTest {

    @Test
    void skipsAnalysisWhenAllFilesExcluded() throws Exception {
        final String file = "src/main/java/Main.java";
        final Environment env = new ExcludingEnvironment(
            new Environment.Mock()
                .withFile(file, "class Main { int x = 0; }")
        );
        MatcherAssert.assertThat(
            "Excluded files must not yield violations",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }
}
