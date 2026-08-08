/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ErrorProneValidator}'s source-level awareness of the
 * stock {@code PatternMatchingInstanceof} check. The check suggests
 * pattern-matching {@code instanceof}, a Java 16+ feature, so it must not fire
 * when the project compiles at an older source level (issue #1716).
 * @since 1.0
 */
final class ErrorPronePatternMatchingInstanceofTest {

    /**
     * Name of the ErrorProne check under test.
     */
    private static final String CHECK = "PatternMatchingInstanceof";

    /**
     * Path of the sample file, relative to the base directory.
     */
    private static final String FILE = "src/main/java/foo/Sample.java";

    /**
     * A class whose {@code instanceof} followed by a cast to the same type is
     * exactly what {@code PatternMatchingInstanceof} suggests rewriting.
     */
    private static final String SOURCE = String.join(
        System.lineSeparator(),
        "package foo;",
        "/**",
        " * Sample.",
        " * @since 1.0",
        " */",
        "public final class Sample {",
        "    /**",
        "     * Compare with another object.",
        "     * @param other The other object",
        "     * @return TRUE if the same",
        "     */",
        "    public boolean same(final Object other) {",
        "        return other instanceof Sample",
        "            && ((Sample) other).hashCode() == this.hashCode();",
        "    }",
        "}"
    );

    @Test
    void skipsPatternMatchingInstanceofOnSourceEight() throws Exception {
        MatcherAssert.assertThat(
            "PatternMatchingInstanceof must not fire when source is 8",
            this.checks("8"),
            Matchers.not(Matchers.hasItem(ErrorPronePatternMatchingInstanceofTest.CHECK))
        );
    }

    @Test
    void skipsPatternMatchingInstanceofOnLegacySourceEight() throws Exception {
        MatcherAssert.assertThat(
            "PatternMatchingInstanceof must not fire when source is 1.8",
            this.checks("1.8"),
            Matchers.not(Matchers.hasItem(ErrorPronePatternMatchingInstanceofTest.CHECK))
        );
    }

    @Test
    void reportsPatternMatchingInstanceofOnModernSource() throws Exception {
        MatcherAssert.assertThat(
            "PatternMatchingInstanceof must fire when source is 17",
            this.checks("17"),
            Matchers.hasItem(ErrorPronePatternMatchingInstanceofTest.CHECK)
        );
    }

    /**
     * Run the validator over the sample file with the given source level and
     * return the names of the checks that produced violations.
     * @param source Value for {@code maven.compiler.source}, or {@code null}
     * @return Names of the checks that fired
     * @throws IOException If some IO problem
     */
    private Collection<String> checks(final String source) throws IOException {
        final Environment.Mock mock = new Environment.Mock();
        if (source != null) {
            mock.withParam("maven.compiler.source", source);
        }
        final Environment env = mock.withFile(
            ErrorPronePatternMatchingInstanceofTest.FILE,
            ErrorPronePatternMatchingInstanceofTest.SOURCE
        );
        final Collection<Violation> violations = new ErrorProneValidator(env).validate(
            Collections.singletonList(
                new File(env.basedir(), ErrorPronePatternMatchingInstanceofTest.FILE)
            )
        );
        final Collection<String> names = new ArrayList<>(violations.size());
        for (final Violation violation : violations) {
            names.add(violation.name());
        }
        return names;
    }
}
