/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import io.github.artsok.RepeatedIfExceptionsTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

public final class RepeatedIfExceptionsTest {

    @RepeatedIfExceptionsTest(repeats = 3)
    void connectsToLocalServer() {
        MatcherAssert.assertThat(
            "something must be true",
            true,
            Matchers.equalTo(true)
        );
    }
}
