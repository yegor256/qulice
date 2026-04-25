/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import com.jcabi.log.Logger;

public final class PlainLoggerCalls {

    public void run(final String id, final Exception ex) {
        Logger.info(this, "config: %s", id);
        Logger.error(this, "#run(): %[exception]s", ex);
    }
}
