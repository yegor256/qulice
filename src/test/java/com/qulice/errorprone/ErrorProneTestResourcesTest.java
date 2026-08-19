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
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ErrorProneValidator} and the test resources of a
 * project, which it must leave alone without being told to.
 *
 * <p>The same self-assignment in {@code src/main/java} is reported, as
 * {@link ErrorProneValidatorTest} shows.</p>
 *
 * @since 1.0
 */
final class ErrorProneTestResourcesTest {

    @Test
    void ignoresJavaFilesInTestResources() throws Exception {
        final String file = "src/test/resources/com/qulice/Bad.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            "class Bad { private int value; void set(int v) { this.value = this.value; } }"
        );
        MatcherAssert.assertThat(
            "Java file under src/test/resources must not be compiled",
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }
}
