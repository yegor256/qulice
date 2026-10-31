/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class UnnecessaryLocalAllowedResource {

    public PrintWriter printer() {
        final StringWriter writer = new StringWriter();
        return new PrintWriter(writer);
    }
}
