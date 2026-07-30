/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.Serializable;

public final class NonSerializableClass implements Serializable {

    private final Thread worker = new Thread();

    public Thread worker() {
        return this.worker;
    }
}
