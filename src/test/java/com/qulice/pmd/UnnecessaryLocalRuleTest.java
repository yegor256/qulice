/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
import java.util.stream.Collectors;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for UnnecessaryLocalRule.
 * @since 0.24
 */
final class UnnecessaryLocalRuleTest {

    @Test
    void detectsUnnecessaryLocalVariableOnReturn() throws Exception {
        final String file = "UnnecessaryLocal.java";
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/main/java/foo/%s", file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(file)
            ).asString()
        );
        MatcherAssert.assertThat(
            "UnnecessaryLocalRule should fire on a local variable used only in return",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), name))
            ).stream().map(Violation::name).collect(Collectors.toList()),
            Matchers.hasItem("UnnecessaryLocalRule")
        );
    }

    @Test
    void doesNotFireWhenVariableIsUsedInLoop() throws Exception {
        final String file = "UnnecessaryLocalInLoop.java";
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/main/java/foo/%s", file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(file)
            ).asString()
        );
        MatcherAssert.assertThat(
            "UnnecessaryLocalRule should not fire when variable is used in a loop",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), name))
            ).stream().map(Violation::name).collect(Collectors.toList()),
            Matchers.not(Matchers.hasItem("UnnecessaryLocalRule"))
        );
    }

    @Test
    void doesNotFireWhenVariableIsUsedMoreThanOnce() throws Exception {
        final String file = "UnnecessaryLocalUsedTwice.java";
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/main/java/foo/%s", file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(file)
            ).asString()
        );
        MatcherAssert.assertThat(
            "UnnecessaryLocalRule should not fire when variable is used more than once",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), name))
            ).stream().map(Violation::name).collect(Collectors.toList()),
            Matchers.not(Matchers.hasItem("UnnecessaryLocalRule"))
        );
    }
}
