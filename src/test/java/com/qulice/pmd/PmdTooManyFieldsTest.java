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
 * Test case for TooManyFields rule wiring in qulice ruleset.
 * The rule is suppressed on classes extending AbstractMojo but stays
 * active on plain classes.
 * @since 1.0
 */
final class PmdTooManyFieldsTest {

    @Test
    void doesNotFireOnMojoWithManyFields() throws Exception {
        final String file = "TooManyFieldsInMojo.java";
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/main/java/foo/%s", file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(file)
            ).asString()
        );
        MatcherAssert.assertThat(
            "TooManyFields must not fire on a class extending AbstractMojo",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), name))
            ).stream().map(Violation::name).collect(Collectors.toList()),
            Matchers.not(Matchers.hasItem("TooManyFields"))
        );
    }

    @Test
    void firesOnPlainClassWithManyFields() throws Exception {
        final String file = "TooManyFieldsInPlainClass.java";
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/main/java/foo/%s", file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(file)
            ).asString()
        );
        MatcherAssert.assertThat(
            "TooManyFields must fire on a plain class with many fields",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), name))
            ).stream().map(Violation::name).collect(Collectors.toList()),
            Matchers.hasItem("TooManyFields")
        );
    }
}
