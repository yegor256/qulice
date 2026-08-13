/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ErrorProneValidator}.
 * @since 1.0
 */
final class ErrorProneValidatorTest {

    @Test
    void findsViolationInBadJavaFile() throws Exception {
        final String file = "src/main/java/Bad.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            "class Bad { private int value; void set(int v) { this.value = this.value; } }"
        );
        MatcherAssert.assertThat(
            "ErrorProne must flag the self-assignment in Bad.java",
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.not(Matchers.<Violation>empty())
        );
    }

    @Test
    void doesNotFlagCleanJavaFile() throws Exception {
        final String file = "src/main/java/com/qulice/Clean.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            "package com.qulice; final class Clean { int square(final int num) { return num * num; } }"
        );
        final java.util.Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            );
        MatcherAssert.assertThat(
            String.format("Clean code must not produce ErrorProne violations: %s", violations),
            violations,
            Matchers.<Violation>empty()
        );
    }

    @Test
    void doesNotFlagCheckstyleJavadocTag() throws Exception {
        final String file = "src/main/java/com/qulice/Tagged.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            String.join(
                System.lineSeparator(),
                "package com.qulice;",
                "/**",
                " * Sample.",
                " * @since 1.0",
                " * @checkstyle MethodNameCheck (1 line)",
                " */",
                "final class Tagged {",
                "    int square(final int num) { return num * num; }",
                "}"
            )
        );
        final java.util.Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            );
        MatcherAssert.assertThat(
            String.format(
                "@checkstyle Javadoc tag must not trigger ErrorProne violations: %s",
                violations
            ),
            violations,
            Matchers.<Violation>empty()
        );
    }

    @Test
    void doesNotFlagMixedBooleanOperators() throws Exception {
        final String file = "src/main/java/com/qulice/Mixed.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            String.join(
                System.lineSeparator(),
                "package com.qulice;",
                "/**",
                " * Sample.",
                " * @since 1.0",
                " */",
                "final class Mixed {",
                "    boolean hex(final char glyph) {",
                "        return glyph >= '0' && glyph <= '9'",
                "            || glyph >= 'A' && glyph <= 'F';",
                "    }",
                "}"
            )
        );
        final java.util.Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            );
        MatcherAssert.assertThat(
            String.format(
                "Mixed && and || without parens must not trigger OperatorPrecedence: %s",
                violations
            ),
            violations,
            Matchers.<Violation>empty()
        );
    }

    @Test
    void doesNotBlameProjectForTwinPackageInfo() throws Exception {
        final String main = "src/main/java/com/qulice/package-info.java";
        final String test = "src/test/java/com/qulice/package-info.java";
        final String body = String.join(
            System.lineSeparator(),
            "/**",
            " * Sample.",
            " * @since 1.0",
            " */",
            "package com.qulice;"
        );
        final Environment env = new Environment.Mock()
            .withFile(main, body).withFile(test, body);
        final java.util.Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                java.util.Arrays.asList(
                    new File(env.basedir(), main), new File(env.basedir(), test)
                )
            );
        MatcherAssert.assertThat(
            String.format(
                "twin package-info files are legal in Maven and must pass: %s",
                violations
            ),
            violations,
            Matchers.<Violation>empty()
        );
    }

    @Test
    void reportsPlainCompilerError() throws Exception {
        final String file = "src/main/java/com/qulice/Broken.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            String.join(
                System.lineSeparator(),
                "package com.qulice;",
                "/**",
                " * Sample.",
                " * @since 1.0",
                " */",
                "final class Broken {",
                "    int count() { return Missing.value(); }",
                "}"
            )
        );
        MatcherAssert.assertThat(
            "cannot find symbol must not slip through as a green build",
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ).stream().map(Violation::message).toList(),
            Matchers.hasItem(Matchers.containsString("cannot find symbol"))
        );
    }

    @Test
    void reportsPlainCompilerWarning() throws Exception {
        final String file = "src/main/java/com/qulice/Removal.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            String.join(
                System.lineSeparator(),
                "package com.qulice;",
                "/**",
                " * Sample.",
                " * @since 1.0",
                " */",
                "final class Removal {",
                "    @Deprecated(forRemoval = true)",
                "    static int old() { return 1; }",
                "}",
                "final class Caller {",
                "    int call() { return Removal.old(); }",
                "}"
            )
        );
        MatcherAssert.assertThat(
            "deprecated-for-removal warning must not slip through as a green build",
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ).stream().map(Violation::message).toList(),
            Matchers.hasItem(Matchers.containsString("marked for removal"))
        );
    }

    @Test
    void doesNotBlameProjectForOwnCompilerOptions() throws Exception {
        final String file = "src/main/java/com/qulice/Pinned.java";
        final Environment env = new Environment.Mock()
            .withParam("maven.compiler.source", "21").withFile(
                file,
                String.join(
                    System.lineSeparator(),
                    "package com.qulice;",
                    "/**",
                    " * Sample.",
                    " * @since 1.0",
                    " */",
                    "final class Pinned {",
                    "    int square(final int num) { return num * num; }",
                    "}"
                )
            );
        final java.util.Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            );
        MatcherAssert.assertThat(
            String.format(
                "Qulice must not blame the project for its own -source flag: %s",
                violations
            ),
            violations,
            Matchers.<Violation>empty()
        );
    }
}
