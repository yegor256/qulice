/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.BufferedReader;
import java.io.PushbackReader;
import java.io.Reader;

public final class UnnecessaryLocalKeepsResourceOutOfArgument {

    public BufferedReader wrap(final Reader input) {
        final PushbackReader reader = new PushbackReader(input);
        return new BufferedReader(reader);
    }
}
