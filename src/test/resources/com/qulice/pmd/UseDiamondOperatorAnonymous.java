/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.function.Supplier;

public final class UseDiamondOperatorAnonymous {

    public Supplier<String> make() {
        return new Supplier<String>() {
            @Override
            public String get() {
                return "";
            }
        };
    }
}
