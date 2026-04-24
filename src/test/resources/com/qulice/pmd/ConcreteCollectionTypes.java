/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class ConcreteCollectionTypes {
    private final ConcurrentHashMap<String, String> concurrent;

    public ConcreteCollectionTypes(final HashMap<String, String> hmap) {
        this.concurrent = new ConcurrentHashMap<>(hmap);
    }

    public ConcurrentHashMap<String, String> map() {
        return this.concurrent;
    }
}
