/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

final class ManyMethodsChecks {

    @Test
    void firstCheck() {
        MatcherAssert.assertThat("first", "x", Matchers.equalTo("x"));
    }

    @Test
    void secondCheck() {
        MatcherAssert.assertThat("second", "x", Matchers.equalTo("x"));
    }

    @Test
    void thirdCheck() {
        MatcherAssert.assertThat("third", "x", Matchers.equalTo("x"));
    }

    @Test
    void fourthCheck() {
        MatcherAssert.assertThat("fourth", "x", Matchers.equalTo("x"));
    }

    @Test
    void fifthCheck() {
        MatcherAssert.assertThat("fifth", "x", Matchers.equalTo("x"));
    }

    @Test
    void sixthCheck() {
        MatcherAssert.assertThat("sixth", "x", Matchers.equalTo("x"));
    }

    @Test
    void seventhCheck() {
        MatcherAssert.assertThat("seventh", "x", Matchers.equalTo("x"));
    }

    @Test
    void eighthCheck() {
        MatcherAssert.assertThat("eighth", "x", Matchers.equalTo("x"));
    }

    @Test
    void ninthCheck() {
        MatcherAssert.assertThat("ninth", "x", Matchers.equalTo("x"));
    }

    @Test
    void tenthCheck() {
        MatcherAssert.assertThat("tenth", "x", Matchers.equalTo("x"));
    }

    @Test
    void eleventhCheck() {
        MatcherAssert.assertThat("eleventh", "x", Matchers.equalTo("x"));
    }

    @Test
    void twelfthCheck() {
        MatcherAssert.assertThat("twelfth", "x", Matchers.equalTo("x"));
    }

}
