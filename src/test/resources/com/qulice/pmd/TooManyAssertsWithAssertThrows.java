/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class TooManyAssertsWithAssertThrowsTest {

    @Test
    void checksMultipleThingsBeyondTheException() {
        MatcherAssert.assertThat(
            "first check",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> {
                    throw new IllegalStateException("boom");
                }
            ).getMessage(),
            Matchers.equalTo("boom")
        );
        MatcherAssert.assertThat(
            "second check",
            42,
            Matchers.equalTo(42)
        );
        MatcherAssert.assertThat(
            "third check",
            "text",
            Matchers.equalTo("text")
        );
    }
}
