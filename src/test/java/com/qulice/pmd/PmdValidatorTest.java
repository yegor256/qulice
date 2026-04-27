/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for general {@link PmdValidator} behavior that is
 * not tied to a single PMD rule. Per-rule coverage lives in the
 * dedicated {@code Pmd*Test.java} files in this package.
 * @since 0.3
 */
final class PmdValidatorTest {

    @Test
    void findsProblemsInJavaFiles() throws Exception {
        final String file = "src/main/java/Main.java";
        final Environment env = new Environment.Mock()
            .withFile(file, "class Main { int x = 0; }");
        MatcherAssert.assertThat(
            "Violations should be found",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.not(Matchers.<Violation>empty())
        );
    }

    @Test
    void acceptsJavaFilesWithUppercaseExtension() throws Exception {
        final String file = "src/main/java/Main.JAVA";
        final Environment env = new Environment.Mock()
            .withFile(file, "class Main { int x = 0; }");
        final Collection<File> kept = new PmdValidator(env).getNonExcludedFiles(
            Collections.singletonList(new File(env.basedir(), file))
        );
        MatcherAssert.assertThat(
            "Java files with uppercase .JAVA extension should not be skipped",
            kept,
            Matchers.hasSize(1)
        );
    }

    @Test
    void acceptsJavaFilesWithMixedCaseExtension() throws Exception {
        final String file = "src/main/java/Main.Java";
        final Environment env = new Environment.Mock()
            .withFile(file, "class Main { int x = 0; }");
        final Collection<File> kept = new PmdValidator(env).getNonExcludedFiles(
            Collections.singletonList(new File(env.basedir(), file))
        );
        MatcherAssert.assertThat(
            "Java files with mixed-case .Java extension should not be skipped",
            kept,
            Matchers.hasSize(1)
        );
    }

    @Test
    void skipsFilesThatAreNotJava() throws Exception {
        final String file = "src/main/java/notes.txt";
        final Environment env = new Environment.Mock()
            .withFile(file, "hello");
        final Collection<File> kept = new PmdValidator(env).getNonExcludedFiles(
            Collections.singletonList(new File(env.basedir(), file))
        );
        MatcherAssert.assertThat(
            "Non-Java files should be skipped",
            kept,
            Matchers.empty()
        );
    }
}
