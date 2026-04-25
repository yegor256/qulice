/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import com.jcabi.log.Logger;

public final class StringFormatInsideLogger {

    public void run(final String reducer) {
        Logger.error(this, String.format("Reducer: %s", reducer));
    }

    public void runFullyQualified(final int idx) {
        com.jcabi.log.Logger.info(
            this,
            java.lang.String.format("idx: %d", idx)
        );
    }
}
