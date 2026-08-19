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
 * Test case for {@link CheckstyleValidator} and the test resources of
 * a project, which it must leave alone without being told to.
 *
 * <p>The very same source, when it sits in {@code src/main/java},
 * produces violations, as {@link CheckstyleValidatorTest} shows.</p>
 *
 * @since 1.0
 */
final class CheckstyleTestResourcesTest {

    @Test
    void ignoresJavaFilesInTestResources() throws Exception {
        final String file = "src/test/resources/com/qulice/Bad.java";
        final Environment env = new Environment.Mock()
            .withFile(file, "class Bad { public int x = 0; }");
        MatcherAssert.assertThat(
            "Java file under src/test/resources must not be checked",
            new CheckstyleValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }

    @Test
    void ignoresJavaFilesDeepInTestResources() throws Exception {
        final String file = "src/test/resources/foo/bar/Bad.java";
        final Environment env = new Environment.Mock()
            .withFile(file, "class Bad { public int x = 0; }");
        MatcherAssert.assertThat(
            "Java file in a subdirectory of test resources must be skipped",
            new CheckstyleValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }
}
