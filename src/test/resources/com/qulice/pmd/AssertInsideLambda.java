/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.function.Consumer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

final class AssertInsideLambdaTest {

    @Test
    void checksSomethingInsideLambda() throws Exception {
        final Consumer<String> consumer = arg -> {
            MatcherAssert.assertThat(
                "we expect something",
                arg,
                Matchers.containsString("x")
            );
        };
        consumer.accept("x");
    }
}
