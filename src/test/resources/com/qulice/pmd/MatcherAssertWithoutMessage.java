/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

final class MatcherAssertWithoutMessageTest {

    @Test
    void checksWithoutMessage() {
        MatcherAssert.assertThat(
            "hello",
            Matchers.equalTo("hello")
        );
    }
}
