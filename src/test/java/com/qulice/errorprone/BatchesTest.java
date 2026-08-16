/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Environment;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Batches}.
 * @since 1.0
 */
final class BatchesTest {

    @Test
    void keepsEveryTestRootApart() {
        final Environment env = new Environment.Mock()
            .withTestdir("src/mock/java");
        final Map<String, List<File>> batches = new Batches(
            env,
            Arrays.asList(
                new File(env.basedir(), "src/main/java/com/qulice/Foo.java"),
                new File(env.basedir(), "src/test/java/com/qulice/Foo.java"),
                new File(env.basedir(), "src/mock/java/com/qulice/Foo.java")
            )
        ).split();
        MatcherAssert.assertThat(
            String.format(
                "each source root must compile on its own: %s", batches
            ),
            batches.keySet(),
            Matchers.contains("main", "test-1-src-mock-java", "test-2-src-test")
        );
    }

    @Test
    void namesBatchesAfterTheirRoots() {
        final Environment env = new Environment.Mock()
            .withTestdir("src/mock/java");
        final Map<String, List<File>> batches = new Batches(
            env,
            Collections.singletonList(
                new File(env.basedir(), "src/mock/java/com/qulice/Foo.java")
            )
        ).split();
        MatcherAssert.assertThat(
            String.format(
                "the batch of a root must be named after it: %s", batches
            ),
            batches.keySet(),
            Matchers.contains("test-1-src-mock-java")
        );
    }

    @Test
    void skipsEmptyBatches() {
        final Environment env = new Environment.Mock()
            .withTestdir("src/mock/java");
        final Map<String, List<File>> batches = new Batches(
            env,
            Collections.singletonList(
                new File(env.basedir(), "src/main/java/com/qulice/Foo.java")
            )
        ).split();
        MatcherAssert.assertThat(
            String.format("a root without sources needs no pass: %s", batches),
            batches.keySet(),
            Matchers.contains("main")
        );
    }

    @Test
    void givesNestedRootItsOwnBatch() {
        final Environment env = new Environment.Mock()
            .withTestdir("src/test/java/it");
        final File outer = new File(
            env.basedir(), "src/test/java/com/qulice/Foo.java"
        );
        final File inner = new File(
            env.basedir(), "src/test/java/it/com/qulice/Foo.java"
        );
        final Map<String, List<File>> batches = new Batches(
            env, Arrays.asList(outer, inner)
        ).split();
        MatcherAssert.assertThat(
            String.format(
                "a root nested in another must claim its files: %s", batches
            ),
            batches.get("test-1-src-test-java-it"),
            Matchers.contains(inner)
        );
    }
}
