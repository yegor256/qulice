/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class UnnecessaryLocalOpenedResource {

    public LineNumberReader open(final Path path) throws IOException {
        final BufferedReader reader = Files.newBufferedReader(path);
        return new LineNumberReader(reader);
    }
}
