/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

final class ManyMethodsTest {

    @Test
    void firstCheck() {
        MatcherAssert.assertThat("a", "a", Matchers.equalTo("a"));
    }

    @Test
    void secondCheck() {
        MatcherAssert.assertThat("b", "b", Matchers.equalTo("b"));
    }

    @Test
    void thirdCheck() {
        MatcherAssert.assertThat("c", "c", Matchers.equalTo("c"));
    }

    @Test
    void fourthCheck() {
        MatcherAssert.assertThat("d", "d", Matchers.equalTo("d"));
    }

    @Test
    void fifthCheck() {
        MatcherAssert.assertThat("e", "e", Matchers.equalTo("e"));
    }

    @Test
    void sixthCheck() {
        MatcherAssert.assertThat("f", "f", Matchers.equalTo("f"));
    }

    @Test
    void seventhCheck() {
        MatcherAssert.assertThat("g", "g", Matchers.equalTo("g"));
    }

    @Test
    void eighthCheck() {
        MatcherAssert.assertThat("h", "h", Matchers.equalTo("h"));
    }

    @Test
    void ninthCheck() {
        MatcherAssert.assertThat("i", "i", Matchers.equalTo("i"));
    }

    @Test
    void tenthCheck() {
        MatcherAssert.assertThat("j", "j", Matchers.equalTo("j"));
    }

    @Test
    void eleventhCheck() {
        MatcherAssert.assertThat("k", "k", Matchers.equalTo("k"));
    }
}
