/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.ArrayList;

public final class UnderstandsMethodReferences {
    public void test() {
        new ArrayList<String>().forEach(
            UnderstandsMethodReferences::other
        );
    }
    private static void other() {
        // body
    }
}
