/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Violation.Default}.
 * @since 0.24
 */
final class ViolationTest {

    /**
     * Validator name reused across the tests.
     */
    private static final String CHECKSTYLE = "checkstyle";

    /**
     * Check name reused across the tests.
     */
    private static final String RULE = "LineLength";

    /**
     * File name reused across the tests.
     */
    private static final String FILE = "Foo.java";

    /**
     * Line number reused across the tests.
     */
    private static final String LINE = "10";

    /**
     * Validation message reused across the tests.
     */
    private static final String MESSAGE = "too long";

    @Test
    void distinguishesViolationsInSameValidatorByFile() {
        MatcherAssert.assertThat(
            "violations from same validator on different files cannot be equal",
            new Violation.Default(
                ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                "Alpha.java", ViolationTest.LINE, ViolationTest.MESSAGE
            ).compareTo(
                new Violation.Default(
                    ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                    "Beta.java", ViolationTest.LINE, ViolationTest.MESSAGE
                )
            ),
            Matchers.not(Matchers.is(0))
        );
    }

    @Test
    void distinguishesViolationsInSameFileByLine() {
        MatcherAssert.assertThat(
            "violations on same file at different lines cannot be equal",
            new Violation.Default(
                ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                ViolationTest.FILE, "10", ViolationTest.MESSAGE
            ).compareTo(
                new Violation.Default(
                    ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                    ViolationTest.FILE, "20", ViolationTest.MESSAGE
                )
            ),
            Matchers.not(Matchers.is(0))
        );
    }

    @Test
    void distinguishesViolationsOnSameLineByMessage() {
        MatcherAssert.assertThat(
            "violations on same line with different messages cannot be equal",
            new Violation.Default(
                ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                ViolationTest.FILE, ViolationTest.LINE, "first issue"
            ).compareTo(
                new Violation.Default(
                    ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                    ViolationTest.FILE, ViolationTest.LINE, "second issue"
                )
            ),
            Matchers.not(Matchers.is(0))
        );
    }

    @Test
    void sortsByValidatorFirst() {
        final List<Violation> sorted = Arrays.asList(
            new Violation.Default(
                "pmd", ViolationTest.RULE,
                "Z.java", "1", ViolationTest.MESSAGE
            ),
            new Violation.Default(
                ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                "A.java", "999", ViolationTest.MESSAGE
            )
        );
        sorted.sort(null);
        MatcherAssert.assertThat(
            "validator name must drive primary ordering",
            sorted.get(0).validator(),
            Matchers.equalTo(ViolationTest.CHECKSTYLE)
        );
    }

    @Test
    void sortsByFileWhenValidatorMatches() {
        final List<Violation> sorted = Arrays.asList(
            new Violation.Default(
                ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                "Zeta.java", "1", ViolationTest.MESSAGE
            ),
            new Violation.Default(
                ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                "Alpha.java", "1", ViolationTest.MESSAGE
            )
        );
        sorted.sort(null);
        MatcherAssert.assertThat(
            "within same validator, file name determines ordering",
            sorted.get(0).file(),
            Matchers.equalTo("Alpha.java")
        );
    }

    @Test
    void sortsByLineWhenValidatorAndFileMatch() {
        final List<Violation> sorted = Arrays.asList(
            new Violation.Default(
                ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                ViolationTest.FILE, "20", ViolationTest.MESSAGE
            ),
            new Violation.Default(
                ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                ViolationTest.FILE, "5", ViolationTest.MESSAGE
            )
        );
        sorted.sort(null);
        MatcherAssert.assertThat(
            "within same file, numeric line number determines ordering",
            sorted.get(0).lines(),
            Matchers.equalTo("5")
        );
    }

    @Test
    void treatsEqualViolationsAsEqualUnderCompare() {
        MatcherAssert.assertThat(
            "violations with identical fields must be ordered equally",
            new Violation.Default(
                ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                ViolationTest.FILE, ViolationTest.LINE, ViolationTest.MESSAGE
            ).compareTo(
                new Violation.Default(
                    ViolationTest.CHECKSTYLE, ViolationTest.RULE,
                    ViolationTest.FILE, ViolationTest.LINE, ViolationTest.MESSAGE
                )
            ),
            Matchers.is(0)
        );
    }
}
