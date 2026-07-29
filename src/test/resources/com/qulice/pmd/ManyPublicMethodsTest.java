/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public final class ManyPublicMethodsTest {

    @Test
    public void firstCheck() {
        MatcherAssert.assertThat("a", "a", Matchers.equalTo("a"));
    }

    @Test
    public void secondCheck() {
        MatcherAssert.assertThat("b", "b", Matchers.equalTo("b"));
    }

    @Test
    public void thirdCheck() {
        MatcherAssert.assertThat("c", "c", Matchers.equalTo("c"));
    }

    @Test
    public void fourthCheck() {
        MatcherAssert.assertThat("d", "d", Matchers.equalTo("d"));
    }

    @Test
    public void fifthCheck() {
        MatcherAssert.assertThat("e", "e", Matchers.equalTo("e"));
    }

    @Test
    public void sixthCheck() {
        MatcherAssert.assertThat("f", "f", Matchers.equalTo("f"));
    }

    @Test
    public void seventhCheck() {
        MatcherAssert.assertThat("g", "g", Matchers.equalTo("g"));
    }

    @Test
    public void eighthCheck() {
        MatcherAssert.assertThat("h", "h", Matchers.equalTo("h"));
    }

    @Test
    public void ninthCheck() {
        MatcherAssert.assertThat("i", "i", Matchers.equalTo("i"));
    }

    @Test
    public void tenthCheck() {
        MatcherAssert.assertThat("j", "j", Matchers.equalTo("j"));
    }

    @Test
    public void eleventhCheck() {
        MatcherAssert.assertThat("k", "k", Matchers.equalTo("k"));
    }
}
