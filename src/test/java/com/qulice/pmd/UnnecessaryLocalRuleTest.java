/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
import java.util.List;
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

    /**
     * The name of the rule under test.
     */
    private static final String RULE = "UnnecessaryLocalRule";

    /**
     * Path template for the fixture file in the mock environment.
     */
    private static final String PATH = "src/main/java/foo/%s";

    @Test
    void detectsUnnecessaryLocalVariableOnReturn() throws Exception {
        MatcherAssert.assertThat(
            "UnnecessaryLocalRule should fire on a local variable used only in return",
            this.violations("UnnecessaryLocal.java"),
            Matchers.hasItem(UnnecessaryLocalRuleTest.RULE)
        );
    }

    @Test
    void doesNotFireWhenVariableIsUsedInLoop() throws Exception {
        MatcherAssert.assertThat(
            "UnnecessaryLocalRule should not fire when variable is used in a loop",
            this.violations("UnnecessaryLocalInLoop.java"),
            Matchers.not(Matchers.hasItem(UnnecessaryLocalRuleTest.RULE))
        );
    }

    @Test
    void doesNotFireWhenVariableIsUsedMoreThanOnce() throws Exception {
        MatcherAssert.assertThat(
            "UnnecessaryLocalRule should not fire when variable is used more than once",
            this.violations("UnnecessaryLocalUsedTwice.java"),
            Matchers.not(Matchers.hasItem(UnnecessaryLocalRuleTest.RULE))
        );
    }

    @Test
    void doesNotFireWhenUseIsAcrossAnonymousClassOrLambda() throws Exception {
        MatcherAssert.assertThat(
            "UnnecessaryLocalRule should not fire when the only use is inside an anonymous class or lambda body",
            this.violations("UnnecessaryLocalAcrossAnonymousClass.java"),
            Matchers.not(Matchers.hasItem(UnnecessaryLocalRuleTest.RULE))
        );
    }

    @Test
    void doesNotFireWhenInitialiserCapturesFreshState() throws Exception {
        MatcherAssert.assertThat(
            "UnnecessaryLocalRule should not fire when the initialiser captures a clock or randomness source",
            this.violations("UnnecessaryLocalCapturedTimestamp.java"),
            Matchers.not(Matchers.hasItem(UnnecessaryLocalRuleTest.RULE))
        );
    }

    @Test
    void doesNotFireWhenInterveningCallMutatesSameTarget() throws Exception {
        MatcherAssert.assertThat(
            "UnnecessaryLocalRule should not fire when a later statement calls the same target",
            this.violations("UnnecessaryLocalDestructiveCleanup.java"),
            Matchers.not(Matchers.hasItem(UnnecessaryLocalRuleTest.RULE))
        );
    }

    private List<String> violations(final String file) throws Exception {
        final String name = String.format(UnnecessaryLocalRuleTest.PATH, file);
        final Environment env = new Environment.Mock().withFile(
            name,
            new TextOf(this.getClass().getResourceAsStream(file)).asString()
        );
        return new PmdValidator(env).validate(
            Collections.singletonList(new File(env.basedir(), name))
        ).stream().map(Violation::name).collect(Collectors.toList());
    }
}
