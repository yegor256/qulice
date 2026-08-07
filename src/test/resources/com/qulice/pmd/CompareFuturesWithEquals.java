/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.Map;
import java.util.concurrent.Future;

public final class CompareFuturesWithEquals {

    private final Map<Integer, Future<?>> futures;

    public CompareFuturesWithEquals(final Map<Integer, Future<?>> map) {
        this.futures = map;
    }

    public int threadOf(final Future<?> future) {
        int thread = -1;
        for (final Map.Entry<Integer, Future<?>> entry : this.futures.entrySet()) {
            if (entry.getValue() == future) {
                thread = entry.getKey();
                break;
            }
        }
        return thread;
    }
}
