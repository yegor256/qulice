/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.ArrayList;
import java.util.List;

public final class UseDiamondOperator {

    public List<String> names() {
        return new ArrayList<String>();
    }
}
