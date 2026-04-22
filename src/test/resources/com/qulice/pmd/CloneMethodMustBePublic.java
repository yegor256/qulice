/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public class CloneMethodMustBePublic implements Cloneable {
    @Override
    protected CloneMethodMustBePublic clone() throws CloneNotSupportedException {
        return ((CloneMethodMustBePublic) super.clone());
    }
}
