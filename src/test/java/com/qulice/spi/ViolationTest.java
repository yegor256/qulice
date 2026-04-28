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

    @Test
    void distinguishesViolationsInSameValidatorByFile() {
        final Violation first = new Violation.Default(
            "checkstyle", "LineLength", "Alpha.java", "10", "too long"
        );
        final Violation second = new Violation.Default(
            "checkstyle", "LineLength", "Beta.java", "10", "too long"
        );
        MatcherAssert.assertThat(
            "violations from the same validator on different files cannot compare equal",
            first.compareTo(second),
            Matchers.not(Matchers.is(0))
        );
    }

    @Test
    void distinguishesViolationsInSameFileByLine() {
        final Violation first = new Violation.Default(
            "checkstyle", "LineLength", "Foo.java", "10", "too long"
        );
        final Violation second = new Violation.Default(
            "checkstyle", "LineLength", "Foo.java", "20", "too long"
        );
        MatcherAssert.assertThat(
            "violations on the same file at different lines cannot compare equal",
            first.compareTo(second),
            Matchers.not(Matchers.is(0))
        );
    }

    @Test
    void distinguishesViolationsOnSameLineByMessage() {
        final Violation first = new Violation.Default(
            "checkstyle", "LineLength", "Foo.java", "10", "first issue"
        );
        final Violation second = new Violation.Default(
            "checkstyle", "LineLength", "Foo.java", "10", "second issue"
        );
        MatcherAssert.assertThat(
            "violations on the same line with different messages cannot compare equal",
            first.compareTo(second),
            Matchers.not(Matchers.is(0))
        );
    }

    @Test
    void sortsByValidatorFirst() {
        final Violation pmd = new Violation.Default(
            "pmd", "Rule", "Z.java", "1", "x"
        );
        final Violation checkstyle = new Violation.Default(
            "checkstyle", "Rule", "A.java", "999", "x"
        );
        final List<Violation> sorted = Arrays.asList(pmd, checkstyle);
        sorted.sort(null);
        MatcherAssert.assertThat(
            "validator name must drive primary ordering",
            sorted.get(0).validator(),
            Matchers.equalTo("checkstyle")
        );
    }

    @Test
    void sortsByFileWhenValidatorMatches() {
        final Violation later = new Violation.Default(
            "checkstyle", "Rule", "Zeta.java", "1", "x"
        );
        final Violation earlier = new Violation.Default(
            "checkstyle", "Rule", "Alpha.java", "1", "x"
        );
        final List<Violation> sorted = Arrays.asList(later, earlier);
        sorted.sort(null);
        MatcherAssert.assertThat(
            "within the same validator, file name must determine ordering",
            sorted.get(0).file(),
            Matchers.equalTo("Alpha.java")
        );
    }

    @Test
    void sortsByLineWhenValidatorAndFileMatch() {
        final Violation later = new Violation.Default(
            "checkstyle", "Rule", "Foo.java", "20", "x"
        );
        final Violation earlier = new Violation.Default(
            "checkstyle", "Rule", "Foo.java", "5", "x"
        );
        final List<Violation> sorted = Arrays.asList(later, earlier);
        sorted.sort(null);
        MatcherAssert.assertThat(
            "within the same file, numeric line number must determine ordering",
            sorted.get(0).lines(),
            Matchers.equalTo("5")
        );
    }

    @Test
    void treatsEqualViolationsAsEqualUnderCompare() {
        final Violation one = new Violation.Default(
            "checkstyle", "LineLength", "Foo.java", "10", "too long"
        );
        final Violation two = new Violation.Default(
            "checkstyle", "LineLength", "Foo.java", "10", "too long"
        );
        MatcherAssert.assertThat(
            "violations with identical fields must be ordered equally",
            one.compareTo(two),
            Matchers.is(0)
        );
    }
}
