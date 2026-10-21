/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.PushbackReader;
import java.io.Reader;

public final class UnnecessaryLocalReturnedResource {

    public PushbackReader wrap(final Reader input) {
        final PushbackReader reader = new PushbackReader(input);
        return reader;
    }
}
