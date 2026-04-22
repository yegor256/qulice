/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

final class SomeTest {

    @BeforeClass
    public static void beforeClass(){
        // setup before class
    }

    @Test
    void emptyTest(){
        //test something
    }

    @AfterClass
    public static void afterClass() throws Exception {
        //tear down after class
    }

    @Parameterized.Parameters
    public static Collection parameters() {
        return Collections.EMPTY_LIST;
    }

}
