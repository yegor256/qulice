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
 * Test case for {@link com.qulice.pmd.rules.CloseInlineResourceRule}.
 * @since 0.27.7
 */
final class CloseInlineResourceRuleTest {

    /**
     * The name of the rule under test.
     */
    private static final String RULE = "CloseInlineResourceRule";

    /**
     * Path template for the fixture file in the mock environment.
     */
    private static final String PATH = "src/main/java/foo/%s";

    @Test
    void detectsCloseableInsideMethodChain() throws Exception {
        MatcherAssert.assertThat(
            "CloseInlineResourceRule should fire on inline closeable expressions",
            this.violations("InlineCloseableMethodChain.java"),
            Matchers.hasItem(CloseInlineResourceRuleTest.RULE)
        );
    }

    @Test
    void detectsArgumentAndChainReceiver() throws Exception {
        MatcherAssert.assertThat(
            "CloseInlineResourceRule should catch the argument and chain receiver",
            Collections.frequency(
                this.violations("InlineCloseableMethodChain.java"),
                CloseInlineResourceRuleTest.RULE
            ),
            Matchers.greaterThanOrEqualTo(2)
        );
    }

    @Test
    void allowsTryWithResources() throws Exception {
        MatcherAssert.assertThat(
            "CloseInlineResourceRule should allow try-with-resources",
            this.violations("InlineCloseableTryWithResources.java"),
            Matchers.not(Matchers.hasItem(CloseInlineResourceRuleTest.RULE))
        );
    }

    private List<String> violations(final String file) throws Exception {
        final String name = String.format(CloseInlineResourceRuleTest.PATH, file);
        final Environment env = new Environment.Mock().withFile(
            name,
            new TextOf(this.getClass().getResourceAsStream(file)).asString()
        );
        return new PmdValidator(env).validate(
            Collections.singletonList(new File(env.basedir(), name))
        ).stream().map(Violation::name).collect(Collectors.toList());
    }
}
