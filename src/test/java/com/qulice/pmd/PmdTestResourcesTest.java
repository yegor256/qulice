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
 * Test case for {@link PmdValidator} and the test resources of a
 * project, which it must leave alone without being told to.
 *
 * <p>The same source in {@code src/main/java} does produce violations,
 * as {@link PmdValidatorTest} shows.</p>
 *
 * @since 1.0
 */
final class PmdTestResourcesTest {

    @Test
    void ignoresJavaFilesInTestResources() throws Exception {
        final String file = "src/test/resources/com/qulice/Main.java";
        final Environment env = new Environment.Mock()
            .withFile(file, "class Main { int x = 0; }");
        MatcherAssert.assertThat(
            "Java file under src/test/resources must not be checked",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }

    @Test
    void keepsTestResourcesOutOfTheFileList() throws Exception {
        final String file = "src/test/resources/foo/bar/Main.java";
        final Environment env = new Environment.Mock()
            .withFile(file, "class Main { int x = 0; }");
        MatcherAssert.assertThat(
            "Test resource must not even reach the list of sources",
            new PmdValidator(env).getNonExcludedFiles(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.empty()
        );
    }
}
