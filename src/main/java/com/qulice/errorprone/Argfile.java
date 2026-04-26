/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * A {@code javac} argfile materialised on disk.
 *
 * <p>Wraps a list of command-line tokens and writes them to a single
 * file in {@code javac}'s argfile format — one double-quoted token per
 * line with backslashes and double quotes escaped — so even very long
 * classpaths and source lists stay below the Windows
 * {@code CreateProcess} command-line limit when passed to {@code javac}
 * as {@code @path}. The {@code -J} launcher flags must NOT be included
 * here; {@code javac} forbids them inside argfiles.</p>
 *
 * @since 1.0
 */
public final class Argfile {

    /**
     * Where the argfile will be written.
     */
    private final File place;

    /**
     * Tokens to write, in order, one per line.
     */
    private final List<String> args;

    /**
     * Constructor.
     * @param place Target file path
     * @param args Tokens to write
     */
    public Argfile(final File place, final List<String> args) {
        this.place = place;
        this.args = args;
    }

    /**
     * Write the argfile and return its path.
     * @return The path of the just-written argfile
     */
    public File save() {
        final StringBuilder content = new StringBuilder(this.args.size() * 64);
        for (final String arg : this.args) {
            content.append('"');
            for (int idx = 0; idx < arg.length(); idx += 1) {
                final char chr = arg.charAt(idx);
                if (chr == '\\' || chr == '"') {
                    content.append('\\');
                }
                content.append(chr);
            }
            content.append('"').append(System.lineSeparator());
        }
        try {
            Files.writeString(
                this.place.toPath(),
                content.toString(),
                StandardCharsets.UTF_8
            );
        } catch (final IOException ex) {
            throw new UncheckedIOException(
                String.format("Unable to write argfile %s", this.place), ex
            );
        }
        return this.place;
    }
}
