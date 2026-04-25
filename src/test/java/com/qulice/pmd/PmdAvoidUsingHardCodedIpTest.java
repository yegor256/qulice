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
 * Test case for AvoidUsingHardCodedIP rule wiring in qulice ruleset.
 * The rule is suppressed inside unit-test classes (whose simple name
 * ends with Test, IT, TestCase or ITCase) but stays active in
 * production code.
 * @since 0.26.0
 */
final class PmdAvoidUsingHardCodedIpTest {

    @Test
    void firesOnHardCodedIpInProductionClass() throws Exception {
        final String file = "HasHardCodedIp.java";
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/main/java/foo/%s", file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(file)
            ).asString()
        );
        MatcherAssert.assertThat(
            "AvoidUsingHardCodedIP must fire when a hard-coded IP is used in a production class",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), name))
            ).stream().map(Violation::name).collect(Collectors.toList()),
            Matchers.hasItem("AvoidUsingHardCodedIP")
        );
    }

    @Test
    void doesNotFireOnHardCodedIpInTestClass() throws Exception {
        final String file = "HasHardCodedIpTest.java";
        final Environment.Mock mock = new Environment.Mock();
        final String name = String.format("src/test/java/foo/%s", file);
        final Environment env = mock.withFile(
            name,
            new TextOf(
                this.getClass().getResourceAsStream(file)
            ).asString()
        );
        MatcherAssert.assertThat(
            "AvoidUsingHardCodedIP must not fire when a hard-coded IP is used inside a unit-test class",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), name))
            ).stream().map(Violation::name).collect(Collectors.toList()),
            Matchers.not(Matchers.hasItem("AvoidUsingHardCodedIP"))
        );
    }
}
