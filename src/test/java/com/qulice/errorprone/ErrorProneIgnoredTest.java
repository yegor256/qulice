/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link ErrorProneValidator} and the directories Qulice
 * ignores by default, which it must leave alone without being told to.
 *
 * <p>The same self-assignment in {@code src/main/java} is reported, as
 * {@link ErrorProneValidatorTest} shows.</p>
 *
 * @since 1.0
 */
final class ErrorProneIgnoredTest {

    @ParameterizedTest
    @ValueSource(
        strings = {
            "src/test/resources/com/qulice/Bad.java",
            "src/site/apt/Bad.java",
            "src/it/violations/src/main/java/foo/Bad.java"
        }
    )
    void ignoresJavaFilesInIgnoredDirectories(final String file) throws Exception {
        final Environment env = new Environment.Mock().withFile(
            file,
            "class Bad { private int value; void set(int v) { this.value = this.value; } }"
        );
        MatcherAssert.assertThat(
            String.format("%s must not be compiled", file),
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }
}
