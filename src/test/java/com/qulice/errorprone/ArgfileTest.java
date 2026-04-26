/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Argfile}.
 * @since 1.0
 */
final class ArgfileTest {

    @Test
    void writesQuotedTokensOnePerLine(@TempDir final File dir) throws Exception {
        final File path = new File(dir, "args.txt");
        new Argfile(
            path,
            List.of("-classpath", "C:\\one\\two.jar;C:\\Program Files\\x.jar")
        ).save();
        MatcherAssert.assertThat(
            "argfile must escape backslashes and keep one token per line",
            Files.readString(path.toPath(), StandardCharsets.UTF_8),
            Matchers.equalTo(
                String.join(
                    System.lineSeparator(),
                    "\"-classpath\"",
                    "\"C:\\\\one\\\\two.jar;C:\\\\Program Files\\\\x.jar\"",
                    ""
                )
            )
        );
    }

    @Test
    void escapesEmbeddedDoubleQuotes(@TempDir final File dir) throws Exception {
        final File path = new File(dir, "args.txt");
        new Argfile(path, List.of("a\"b")).save();
        MatcherAssert.assertThat(
            "embedded double quotes must be backslash-escaped",
            Files.readString(path.toPath(), StandardCharsets.UTF_8),
            Matchers.equalTo(
                String.join(System.lineSeparator(), "\"a\\\"b\"", "")
            )
        );
    }
}
