/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.HashMap;
import java.util.Map;

public final class UseConcurrentHashMap {
    public void getMyInstance() {
        final Map<String, String> map = new HashMap<>();
    }
}
