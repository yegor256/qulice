/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class UnnecessaryLocalCapturedBeforeCall {

    private final Path path;

    private final Origin origin;

    public UnnecessaryLocalCapturedBeforeCall(final Path dst, final Origin org) {
        this.path = dst;
        this.origin = org;
    }

    public String apply(final int position) {
        final long start = System.currentTimeMillis();
        final String out = this.origin.apply(position);
        try {
            Files.write(
                this.path,
                String.format(
                    "%s,%d%n", out, System.currentTimeMillis() - start
                ).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        return out;
    }

    public interface Origin {
        String apply(int position);
    }
}
