/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s handling of private
 * constants that are used only from within an inner class —
 * they must not be reported as unused.
 * @since 0.25.1
 */
final class PmdInnerClassConstantsTest {

    @Test
    void doesNotComplainAboutConstantsInInnerClasses() throws Exception {
        final String file = "src/main/java/foo/Foo.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            new TextOf(
                this.getClass().getResourceAsStream("InnerClassConstants.java")
            ).asString()
        );
        MatcherAssert.assertThat(
            "Private constant in inner class is not a violation",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }
}
