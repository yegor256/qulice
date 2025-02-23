/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class PlainJUnitAssertionTestMethod {

    @Test
    public void prohibitPlainJunitAssertionsInTests() throws Exception {
        Assert.assertEquals("errorMessage", "expected", "actual");
    }
}
