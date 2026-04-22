/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * A file that is considered binary.
 *
 * <p>Detects binary files using the classic NULL-byte heuristic: if any
 * of the first {@link #SNIFF} bytes of the file is {@code 0x00}, the file
 * is treated as binary. This is the same rule used by {@code git diff}
 * and GNU {@code grep} to decide whether a file is text or binary. It
 * intentionally misclassifies nothing for typical source code (Java, XML,
 * YAML, JSON, UTF-8) while correctly ignoring images, archives, compiled
 * classes and similar blobs that should not be fed to text-based
 * validators such as Checkstyle's {@code RegexpSinglelineCheck}.</p>
 *
 * <p>A file that does not exist, is not a regular file, or is empty is
 * considered non-binary, so the caller may still attempt to open it and
 * fail fast with a clear error, rather than silently skipping it here.</p>
 *
 * @since 0.24
 */
public final class Binary {

    /**
     * Amount of bytes to sniff from the head of the file.
     */
    private static final int SNIFF = 8192;

    /**
     * The file to inspect.
     */
    private final File file;

    /**
     * Ctor.
     * @param src File to inspect
     */
    public Binary(final File src) {
        this.file = src;
    }

    /**
     * Is the file binary?
     * @return TRUE if the first {@value #SNIFF} bytes contain a NULL byte
     */
    public boolean yes() {
        boolean binary = false;
        if (this.file.isFile()) {
            try (InputStream stream = Files.newInputStream(this.file.toPath())) {
                final byte[] buffer = new byte[Binary.SNIFF];
                final int read = stream.read(buffer);
                for (int idx = 0; idx < read; ++idx) {
                    if (buffer[idx] == 0) {
                        binary = true;
                        break;
                    }
                }
            } catch (final IOException ex) {
                throw new IllegalStateException(
                    String.format(
                        "Failed to inspect file '%s' for binary content",
                        this.file
                    ),
                    ex
                );
            }
        }
        return binary;
    }
}
