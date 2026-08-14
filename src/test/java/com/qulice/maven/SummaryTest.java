/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Summary} class.
 * @since 1.0
 */
final class SummaryTest {

    @Test
    void countsJavaFilesAndRulesOfEveryValidator() {
        MatcherAssert.assertThat(
            "Summary must count the Java files checked and the rules applied",
            new Summary(
                Arrays.asList(
                    new File("Broken.java"),
                    new File("pom.xml"),
                    new File("Fixed.JAVA")
                ),
                Arrays.asList(
                    new FakeResourceValidator("Alpha", 41),
                    new FakeResourceValidator("Beta", 7)
                )
            ).toString(),
            Matchers.equalTo(
                "checked 2 .java files against 48 rules (41 Alpha, 7 Beta)"
            )
        );
    }

    @Test
    void skipsValidatorThatCountsNoRules() {
        MatcherAssert.assertThat(
            "Validator without rules cannot show up in the summary",
            new Summary(
                Collections.singleton(new File("Main.java")),
                Arrays.asList(
                    new FakeResourceValidator("Gamma", 3),
                    new FakeResourceValidator("Delta", 0)
                )
            ).toString(),
            Matchers.equalTo("checked 1 .java file against 3 rules (3 Gamma)")
        );
    }

    @Test
    void mentionsNoRulesWhenNobodyCounts() {
        MatcherAssert.assertThat(
            "Summary without rules cannot carry an empty pair of brackets",
            new Summary(
                Collections.emptyList(),
                Collections.singleton(new FakeResourceValidator("Zeta", 0))
            ).toString(),
            Matchers.equalTo("checked 0 .java files against 0 rules")
        );
    }
}
