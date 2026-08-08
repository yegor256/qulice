/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

final class AssertionAffirmTest {

    @Test
    void checksSomethingWithAffirm() throws Exception {
        new Assertion<>(
            "Must be equal to the expected value",
            "hello",
            Matchers.equalTo("hello")
        ).affirm();
    }
}
