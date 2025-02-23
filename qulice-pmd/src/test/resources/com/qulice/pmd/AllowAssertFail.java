/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class AllowAssertFail {

    @Test
    void prohibitPlainJunitAssertionsInTests() throws Exception {
        Matchers.assertThat("errorMessage", "expected", Matchers.is("actual"));
        Assertions.fail("fail test");
    }
}
