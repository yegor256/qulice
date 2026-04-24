/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Binary}.
 * @since 0.24
 */
final class BinaryTest {

    @Test
    void detectsPngFileAsBinary(@TempDir final Path dir) throws Exception {
        final File file = dir.resolve("pixel.png").toFile();
        Files.write(
            file.toPath(),
            new byte[] {
                (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
                0x00, 0x00, 0x00, 0x0d, 0x49, 0x48, 0x44, 0x52,
            }
        );
        MatcherAssert.assertThat(
            "PNG header must not be treated as a text file",
            new Binary(file).yes(),
            Matchers.is(true)
        );
    }

    @Test
    void detectsJavaSourceAsNotBinary(@TempDir final Path dir)
        throws Exception {
        final File file = dir.resolve("Foo.java").toFile();
        Files.writeString(
            file.toPath(),
            "class Foo { int bar = 42; }".concat(String.valueOf('\n')),
            StandardCharsets.UTF_8
        );
        MatcherAssert.assertThat(
            "plain Java source cannot be treated as binary",
            new Binary(file).yes(),
            Matchers.is(false)
        );
    }

    @Test
    void treatsUnicodeTextWithAccentsAsNotBinary(@TempDir final Path dir)
        throws Exception {
        final File file = dir.resolve("notes.txt").toFile();
        Files.writeString(
            file.toPath(),
            "voilà — árvíztűrő tükörfúrógép".concat(String.valueOf('\n')),
            StandardCharsets.UTF_8
        );
        MatcherAssert.assertThat(
            "UTF-8 multibyte characters must not be mistaken for binary data",
            new Binary(file).yes(),
            Matchers.is(false)
        );
    }

    @Test
    void treatsEmptyFileAsNotBinary(@TempDir final Path dir) throws Exception {
        final File file = dir.resolve("empty.txt").toFile();
        Files.write(file.toPath(), new byte[0]);
        MatcherAssert.assertThat(
            "empty file cannot be classified as binary",
            new Binary(file).yes(),
            Matchers.is(false)
        );
    }

    @Test
    void detectsFileWithNullByteAsBinary(@TempDir final Path dir)
        throws Exception {
        final File file = dir.resolve("blob.dat").toFile();
        Files.write(
            file.toPath(),
            new byte[] {'h', 'e', 'l', 'l', 'o', 0x00, 'w', 'o', 'r', 'l', 'd'}
        );
        MatcherAssert.assertThat(
            "file containing a NULL byte must be classified as binary",
            new Binary(file).yes(),
            Matchers.is(true)
        );
    }

    @Test
    void treatsMissingFileAsNotBinary(@TempDir final Path dir) {
        MatcherAssert.assertThat(
            "non-existing file cannot be classified as binary",
            new Binary(dir.resolve("missing").toFile()).yes(),
            Matchers.is(false)
        );
    }
}
