/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class AssertThrowsWithMessageCheckTest {

    @Test
    void throwsCorrectErrorForRhoAttr() {
        MatcherAssert.assertThat(
            "the message in the error is correct",
            Assertions.assertThrows(
                IllegalStateException.class,
                () -> {
                    throw new IllegalStateException("the attribute must be a number");
                },
                "number must be a number"
            ).getMessage(),
            Matchers.equalTo("the attribute must be a number")
        );
    }
}
