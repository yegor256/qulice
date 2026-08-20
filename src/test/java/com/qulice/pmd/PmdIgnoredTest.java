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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link PmdValidator} and the directories Qulice ignores
 * by default, which it must leave alone without being told to.
 *
 * <p>The same source in {@code src/main/java} does produce violations,
 * as {@link PmdValidatorTest} shows.</p>
 *
 * @since 1.0
 */
final class PmdIgnoredTest {

    @ParameterizedTest
    @ValueSource(
        strings = {
            "src/test/resources/com/qulice/Main.java",
            "src/site/apt/Main.java",
            "src/it/violations/src/main/java/foo/Main.java"
        }
    )
    void ignoresJavaFilesInIgnoredDirectories(final String file) throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(file, "class Main { int x = 0; }");
        MatcherAssert.assertThat(
            String.format("%s must not be checked", file),
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "src/test/resources/foo/bar/Main.java",
            "src/site/resources/Main.java",
            "src/it/foo/Main.java"
        }
    )
    void keepsIgnoredFilesOutOfTheFileList(final String file) throws Exception {
        final Environment env = new Environment.Mock()
            .withFile(file, "class Main { int x = 0; }");
        MatcherAssert.assertThat(
            String.format("%s must not even reach the list of sources", file),
            new PmdValidator(env).getNonExcludedFiles(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.empty()
        );
    }
}
