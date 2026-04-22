/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PomXpathValidator} class.
 * @since 0.6
 */
final class PomXpathValidatorTest {

    /**
     * PomXpathValidator can validate pom.xml with xpath.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void canValidatePomWithXpath() throws Exception {
        final MavenEnvironment env = new MavenEnvironmentMocker()
            .withAsserts(
                Collections.singletonList(
                "/pom:project/pom:dependencies/pom:dependency[pom:artifactId='commons-io']/pom:version[.='1.2.5']/text()"
            )
        ).mock();
        final String pom = new TextOf(
            new ResourceOf("com/qulice/maven/PomXpathValidator/pom.xml")
        ).asString();
        FileUtils.write(
            new File(
                String.format(
                    "%s%spom.xml", env.basedir(), File.separator
                )
            ),
            pom,
            StandardCharsets.UTF_8
        );
        new PomXpathValidator().validate(env);
    }
}
