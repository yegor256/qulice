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
 * Test case for {@link ErrorProneValidator}.
 * @since 1.0
 */
final class ErrorProneValidatorTest {

    @Test
    void findsViolationInBadJavaFile() throws Exception {
        final String file = "src/main/java/Bad.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            "class Bad { private int value; void set(int v) { this.value = this.value; } }"
        );
        MatcherAssert.assertThat(
            "ErrorProne must flag the self-assignment in Bad.java",
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.not(Matchers.<Violation>empty())
        );
    }

    @Test
    void doesNotFlagCleanJavaFile() throws Exception {
        final String file = "src/main/java/com/qulice/Clean.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            "package com.qulice;\nfinal class Clean { int square(final int num) { return num * num; } }\n"
        );
        final java.util.Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            );
        MatcherAssert.assertThat(
            "Clean code must not produce ErrorProne violations: " + violations,
            violations,
            Matchers.<Violation>empty()
        );
    }
}
