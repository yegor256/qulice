/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;

@SuppressWarnings({
    "PMD.AvoidFileStream",
    "PMD.RelianceOnDefaultCharset",
    "PMD.UnusedPrivateMethod"
})
public final class InlineCloseableMethodChain {

    public Iterator<String> read(final Path path) throws IOException {
        return this.parse(new FileReader(path.toFile())).iterator();
    }

    private Parser parse(final FileReader reader) {
        return new Parser(reader);
    }

    private static final class Parser implements Closeable {

        private final FileReader reader;

        Parser(final FileReader reader) {
            this.reader = reader;
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }

        Iterator<String> iterator() {
            return Collections.emptyIterator();
        }
    }
}
