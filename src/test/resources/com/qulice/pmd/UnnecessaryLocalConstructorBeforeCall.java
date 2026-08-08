/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.HashMap;
import java.util.Map;

public final class UnnecessaryLocalConstructorBeforeCall {

    public int apply(final Map<String, String> sample) {
        final Map<String, String> params = new HashMap<>(sample);
        final Report data = new Report(params);
        params.clear();
        return data.size();
    }

    public static final class Report {

        private final Map<String, String> params;

        public Report(final Map<String, String> args) {
            this.params = new HashMap<>(args);
        }

        public int size() {
            return this.params.size();
        }
    }
}
