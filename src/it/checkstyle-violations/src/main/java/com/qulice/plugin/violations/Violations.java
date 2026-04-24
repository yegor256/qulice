/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.plugin.violations;

import com.google.common.collect.Lists;
import java.util.ArrayList;

public class Violations {
    public void test() {
        System.setProperty("test", "test value");
    }

    /**
     * Test method.
     */
    public final void foreach() {
        for (String txt : new String[] {"test"}) {
            System.out.println(txt);
        }
    }

    /**
     * Missing final in catch.
     */
    public final void catchFinal() {
        try {
            Integer.parseInt("123");
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(ex);
        }
    }
    /**
     * ArrayList without initializer.
     */
    public final void arrayLists() {
        System.out.println(new ArrayList<Integer>());
        System.out.println(new java.util.ArrayList<Integer>());
        System.out.println(new ArrayList<Integer>(1));
        System.out.println(new java.util.ArrayList<Integer>(2));
    }

    /**
     * Guava array list without initializer.
     */
    public final void guavaArrayLists() {
        System.out.println(Lists.newArrayList());
        System.out.println(com.google.common.collect.Lists.newArrayList());
        System.out.println(Lists.newArrayList(1, 2));
        System.out.println(com.google.common.collect.Lists.newArrayList(3, 4));
    }
}
