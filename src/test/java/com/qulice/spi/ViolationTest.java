/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Violation}.
 * @since 0.24
 */
final class ViolationTest {

    /**
     * Validator name reused across the cases below.
     */
    private static final String VALIDATOR = "checkstyle";

    /**
     * Check name reused across the cases below.
     */
    private static final String CHECK = "Indent";

    /**
     * Path to a sample source file used in equality cases.
     */
    private static final String FOO = "/src/main/java/Foo.java";

    /**
     * Validation message reused for ordering cases.
     */
    private static final String MESSAGE = "msg";

    @Test
    void distinguishesViolationsWithSameValidator() {
        MatcherAssert.assertThat(
            "two violations differing only by file or line must not tie",
            new Violation.Default(
                ViolationTest.VALIDATOR, "MissingJavadoc",
                "/src/main/java/Bar.java", "10", "alpha"
            ).compareTo(
                new Violation.Default(
                    ViolationTest.VALIDATOR, "MissingJavadoc",
                    "/src/main/java/Baz.java", "20", "beta"
                )
            ),
            Matchers.not(Matchers.equalTo(0))
        );
    }

    @Test
    void ordersViolationsByFileWhenValidatorMatches() {
        final List<Violation> sorted = Arrays.asList(
            new Violation.Default(
                ViolationTest.VALIDATOR, ViolationTest.CHECK,
                "/src/main/java/Beta.java", "1", ViolationTest.MESSAGE
            ),
            new Violation.Default(
                ViolationTest.VALIDATOR, ViolationTest.CHECK,
                "/src/main/java/Alpha.java", "1", ViolationTest.MESSAGE
            )
        );
        Collections.sort(sorted);
        MatcherAssert.assertThat(
            "violations sharing a validator must be sorted by file path",
            sorted.get(0).file(),
            Matchers.equalTo("/src/main/java/Alpha.java")
        );
    }

    @Test
    void ordersViolationsByLineWhenFileMatches() {
        final List<Violation> sorted = Arrays.asList(
            new Violation.Default(
                ViolationTest.VALIDATOR, ViolationTest.CHECK,
                ViolationTest.FOO, "42", ViolationTest.MESSAGE
            ),
            new Violation.Default(
                ViolationTest.VALIDATOR, ViolationTest.CHECK,
                ViolationTest.FOO, "5", ViolationTest.MESSAGE
            )
        );
        Collections.sort(sorted);
        MatcherAssert.assertThat(
            "violations from the same file must be sorted by line number",
            sorted.get(0).lines(),
            Matchers.equalTo("5")
        );
    }

    @Test
    void comparesEqualWhenAllFieldsMatch() {
        MatcherAssert.assertThat(
            "violations with identical fields must compare as equal",
            new Violation.Default(
                "pmd", "UnusedLocal", ViolationTest.FOO, "7", "unused"
            ).compareTo(
                new Violation.Default(
                    "pmd", "UnusedLocal", ViolationTest.FOO, "7", "unused"
                )
            ),
            Matchers.equalTo(0)
        );
    }

    @Test
    void keepsValidatorAsPrimarySortKey() {
        final List<Violation> sorted = Arrays.asList(
            new Violation.Default(
                "pmd", "X", "/src/main/java/Z.java", "999", "z"
            ),
            new Violation.Default(
                ViolationTest.VALIDATOR, "Y",
                "/src/main/java/A.java", "1", "a"
            )
        );
        Collections.sort(sorted);
        MatcherAssert.assertThat(
            "validator name must remain the primary ordering key",
            sorted.get(0).validator(),
            Matchers.equalTo(ViolationTest.VALIDATOR)
        );
    }
}
