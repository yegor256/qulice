/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.Arrays;
import java.util.List;

public final class ToleratesDuplicateLiterals {

    public List<String> names() {
        return Arrays.asList(
            "Jeffrey",
            "Jeffrey",
            "Jeffrey",
            "Jeffrey",
            "Jeffrey",
            "Jeffrey",
            "Jeffrey"
        );
    }
}
