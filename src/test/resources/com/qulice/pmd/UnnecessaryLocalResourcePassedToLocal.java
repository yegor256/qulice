/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public final class UnnecessaryLocalResourcePassedToLocal {

    public String pair(final Reader input) throws IOException {
        final PushbackReader reader = new PushbackReader(input);
        final BufferedReader buffer = new BufferedReader(reader);
        return buffer.readLine() + buffer.readLine();
    }
}
