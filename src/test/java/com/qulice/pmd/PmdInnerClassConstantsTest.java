/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.google.common.base.Joiner;
import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
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
            Joiner.on('\n').join(
                "package foo;",
                "interface Foo {",
                "  final class Bar implements Foo {",
                "    private static final Pattern TEST =",
                "      Pattern.compile(\"hey\");",
                "    public String doSomething() {",
                "      return Foo.Bar.TEST.toString();",
                "    }",
                "  }",
                "}"
            )
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
