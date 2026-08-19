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
 * Test case for ExcessiveParameterList rule wiring in qulice ruleset.
 * The rule is suppressed on constructors but stays active on methods.
 * @since 1.0
 */
final class PmdExcessiveParameterListTest {

    @Test
    void doesNotFireOnConstructorWithManyParameters() throws Exception {
        final String file = "ExcessiveParameterListInConstructor.java";
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/main/java/foo/%s", file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(file)
            ).asString()
        );
        MatcherAssert.assertThat(
            "ExcessiveParameterList must not fire on a constructor with many parameters",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), name))
            ).stream().map(Violation::name).collect(Collectors.toList()),
            Matchers.not(Matchers.hasItem("ExcessiveParameterList"))
        );
    }

    @Test
    void firesOnMethodWithManyParameters() throws Exception {
        final String file = "ExcessiveParameterListInMethod.java";
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/main/java/foo/%s", file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(file)
            ).asString()
        );
        MatcherAssert.assertThat(
            "ExcessiveParameterList must fire on a method with many parameters",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), name))
            ).stream().map(Violation::name).collect(Collectors.toList()),
            Matchers.hasItem("ExcessiveParameterList")
        );
    }
}
