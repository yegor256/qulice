/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
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
    void countsRulesItApplies() throws Exception {
        MatcherAssert.assertThat(
            "ErrorProne must tell how many bug patterns it applies",
            new ErrorProneValidator(new Environment.Mock()).rules(),
            Matchers.greaterThan(100)
        );
    }

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
        final Collection<Violation> violations =
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
        final Collection<Violation> violations =
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
        final Collection<Violation> violations =
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
        final Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Arrays.asList(
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
    void doesNotBlameProjectForPackageInfoInExtraTestRoot() throws Exception {
        final String main = "src/main/java/com/qulice/package-info.java";
        final String mock = "src/mock/java/com/qulice/package-info.java";
        final String body = String.join(
            System.lineSeparator(),
            "/**",
            " * Sample.",
            " * @since 1.0",
            " */",
            "package com.qulice;"
        );
        final Environment env = new Environment.Mock()
            .withTestdir("src/mock/java")
            .withFile(main, body).withFile(mock, body);
        final Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Arrays.asList(
                    new File(env.basedir(), main), new File(env.basedir(), mock)
                )
            );
        MatcherAssert.assertThat(
            String.format(
                "a test source root outside src/test must compile apart: %s",
                violations
            ),
            violations,
            Matchers.<Violation>empty()
        );
    }

    @Test
    void doesNotBlameProjectForTwinPackageInfoInTestRoots() throws Exception {
        final String test = "src/test/java/com/qulice/package-info.java";
        final String mock = "src/mock/java/com/qulice/package-info.java";
        final String body = String.join(
            System.lineSeparator(),
            "/**",
            " * Sample.",
            " * @since 1.0",
            " */",
            "package com.qulice;"
        );
        final Environment env = new Environment.Mock()
            .withTestdir("src/mock/java")
            .withFile(test, body).withFile(mock, body);
        final Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Arrays.asList(
                    new File(env.basedir(), test), new File(env.basedir(), mock)
                )
            );
        MatcherAssert.assertThat(
            String.format(
                "two test source roots must compile apart from each other: %s",
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
    void doesNotFlagNonAsciiIdentifiers() throws Exception {
        final String file = "src/main/java/com/qulice/Greek.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            String.join(
                System.lineSeparator(),
                "package com.qulice;",
                "/**",
                " * Sample.",
                " * @since 1.0",
                " */",
                "final class Greek {",
                "    int φ() { return 1; }",
                "}"
            )
        );
        final Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            );
        MatcherAssert.assertThat(
            String.format(
                "a domain whose own notation is not ASCII must pass: %s",
                violations
            ),
            violations,
            Matchers.<Violation>empty()
        );
    }

    @Test
    void letsProjectSwitchBugPatternOff() throws Exception {
        final String file = "src/main/java/com/qulice/Assigned.java";
        final Environment env = new Environment.Mock()
            .withParam("qulice.errorprone", "-Xep:SelfAssignment:OFF").withFile(
                file,
                String.join(
                    System.lineSeparator(),
                    "package com.qulice;",
                    "/**",
                    " * Sample.",
                    " * @since 1.0",
                    " */",
                    "final class Assigned {",
                    "    private int value;",
                    "    void set(final int num) { this.value = this.value; }",
                    "}"
                )
            );
        final Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            );
        MatcherAssert.assertThat(
            String.format(
                "a project must be able to switch a bug pattern off: %s",
                violations
            ),
            violations,
            Matchers.<Violation>empty()
        );
    }

    @Test
    void letsProjectSwitchBugPatternBackOn() throws Exception {
        final String file = "src/main/java/com/qulice/Ordered.java";
        final Environment env = new Environment.Mock()
            .withParam("qulice.errorprone", "-Xep:OperatorPrecedence:ERROR").withFile(
                file,
                String.join(
                    System.lineSeparator(),
                    "package com.qulice;",
                    "/**",
                    " * Sample.",
                    " * @since 1.0",
                    " */",
                    "final class Ordered {",
                    "    boolean hex(final char glyph) {",
                    "        return glyph >= '0' && glyph <= '9'",
                    "            || glyph >= 'A' && glyph <= 'F';",
                    "    }",
                    "}"
                )
            );
        MatcherAssert.assertThat(
            "a project must have the final say on the patterns Qulice disables",
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ).stream().map(Violation::message).toList(),
            Matchers.hasItem(Matchers.containsString("OperatorPrecedence"))
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
        final Collection<Violation> violations =
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

    @Test
    void compilesTestsAtTheirOwnSourceLevel() throws Exception {
        final String main = "src/main/java/com/qulice/Old.java";
        final String test = "src/test/java/com/qulice/OldTest.java";
        final Environment env = new Environment.Mock()
            .withParam("maven.compiler.release", "8")
            .withParam("maven.compiler.testRelease", "17")
            .withFile(main, ErrorProneValidatorTest.ancient("Old"))
            .withFile(test, ErrorProneValidatorTest.modern("OldTest"));
        final Collection<Violation> violations =
            new ErrorProneValidator(env).validate(
                Arrays.asList(
                    new File(env.basedir(), main), new File(env.basedir(), test)
                )
            );
        MatcherAssert.assertThat(
            String.format(
                "tests pinned to a higher level must compile at it: %s",
                violations
            ),
            violations,
            Matchers.<Violation>empty()
        );
    }

    @Test
    void keepsMainSourcesAtTheirOwnLevel() throws Exception {
        final String file = "src/main/java/com/qulice/Modern.java";
        final Environment env = new Environment.Mock()
            .withParam("maven.compiler.release", "8")
            .withParam("maven.compiler.testRelease", "17")
            .withFile(file, ErrorProneValidatorTest.modern("Modern"));
        MatcherAssert.assertThat(
            "the level of the tests must not leak into the main batch",
            new ErrorProneValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ).stream().map(Violation::message).toList(),
            Matchers.hasItem(Matchers.containsString("text block"))
        );
    }

    private static String ancient(final String name) {
        return String.join(
            System.lineSeparator(),
            "package com.qulice;",
            "/**",
            " * Sample.",
            " * @since 1.0",
            " */",
            String.format("final class %s {", name),
            "    int square(final int num) { return num * num; }",
            "}"
        );
    }

    private static String modern(final String name) {
        return String.join(
            System.lineSeparator(),
            "package com.qulice;",
            "/**",
            " * Sample.",
            " * @since 1.0",
            " */",
            String.format("final class %s {", name),
            "    String text() {",
            "        return \"\"\"",
            "            hello",
            "            \"\"\";",
            "    }",
            "}"
        );
    }
}
