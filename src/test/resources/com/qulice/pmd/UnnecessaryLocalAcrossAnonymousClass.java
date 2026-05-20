/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.IOException;
import java.util.concurrent.Callable;

public final class UnnecessaryLocalAcrossAnonymousClass {

    public Callable<String> capture(final Source src) throws IOException {
        final String author = src.author();
        return new Callable<String>() {
            @Override
            public String call() {
                return author;
            }
        };
    }

    public Runnable captureInLambda(final Source src) throws IOException {
        final String author = src.author();
        return () -> System.out.println(author);
    }

    public interface Source {
        String author() throws IOException;
    }
}
