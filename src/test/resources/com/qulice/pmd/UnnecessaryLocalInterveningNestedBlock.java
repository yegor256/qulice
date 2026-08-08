/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class UnnecessaryLocalInterveningNestedBlock {

    private final boolean quiet;

    public UnnecessaryLocalInterveningNestedBlock(final boolean flag) {
        this.quiet = flag;
    }

    public void analyze(final Sink sink) {
        final String console = this.build(sink);
        sink.run();
        if (!this.quiet) {
            sink.remove(console);
        }
    }

    private String build(final Sink sink) {
        sink.add("console");
        return "console";
    }

    public interface Sink {
        void add(String name);

        void run();

        void remove(String name);
    }
}
