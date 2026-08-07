/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class UnnecessaryLocalCapturedFieldAccess {

    public String captured(final Action action) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream original = System.out;
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    public interface Action {
        void run() throws IOException;
    }
}
