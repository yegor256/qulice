/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link UnusedPackagePrivateClasses}.
 *
 * @since 1.0
 */
final class UnusedPackagePrivateClassesTest {

    @Test
    void reportsClassThatNobodyMentions() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Lonely.java",
            this.source("foo", "final class Lonely { }")
        ).withFile(
            "src/main/java/foo/Main.java",
            this.source("foo", "public class Main { }")
        );
        MatcherAssert.assertThat(
            "a package-private class nobody mentions must be reported",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.hasItem(
                new ViolationMatcher(
                    "This package-private class \"Lonely\" is not used anywhere in its package",
                    "Lonely.java", "2", "UnusedPackagePrivateClassCheck"
                )
            )
        );
    }

    @Test
    void acceptsClassUsedByItsNeighbour() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Helper.java",
            this.source("foo", "final class Helper { }")
        ).withFile(
            "src/main/java/foo/Main.java",
            this.source(
                "foo", "public class Main { Helper one() { return new Helper(); } }"
            )
        );
        MatcherAssert.assertThat(
            "a class used by another class of the package must be accepted",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.empty()
        );
    }

    @Test
    void acceptsClassUsedByAnotherTypeOfTheSameFile() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Main.java",
            this.source(
                "foo",
                "public class Main { Helper one() { return new Helper(); } }",
                "final class Helper { }"
            )
        );
        MatcherAssert.assertThat(
            "a class used by its own file must be accepted",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.empty()
        );
    }

    @Test
    void acceptsPublicClass() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Main.java",
            this.source("foo", "public class Main { }")
        );
        MatcherAssert.assertThat(
            "a public class must be accepted, wherever its users are",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.empty()
        );
    }

    @Test
    void reportsClassThatOnlyMentionsItself() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Lonely.java",
            this.source(
                "foo", "final class Lonely { Lonely twin() { return new Lonely(); } }"
            )
        );
        MatcherAssert.assertThat(
            "a self-reference must not count as a usage",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.hasSize(1)
        );
    }

    @Test
    void reportsUnusedInterfaceEnumAndRecord() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Main.java",
            this.source(
                "foo",
                "public class Main { }",
                "interface Shape { }",
                "enum Colors { RED }",
                "record Point(int width) { }"
            )
        );
        MatcherAssert.assertThat(
            "an interface, an enum and a record must be reported too",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.hasSize(3)
        );
    }

    @Test
    void ignoresTestClassByItsName() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/test/java/foo/LonelyTest.java",
            this.source("foo", "final class LonelyTest { }")
        );
        MatcherAssert.assertThat(
            "a class named as a test must be accepted",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.empty()
        );
    }

    @Test
    void ignoresTestClassByItsAnnotation() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/test/java/foo/Suite.java",
            this.source("foo", "final class Suite { @Test void works() { } }")
        );
        MatcherAssert.assertThat(
            "a class with a JUnit annotation must be accepted",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.empty()
        );
    }

    @Test
    void acceptsClassUsedOnlyByTestOfTheSamePackage() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Helper.java",
            this.source("foo", "final class Helper { }")
        ).withFile(
            "src/test/java/foo/HelperTest.java",
            this.source(
                "foo", "final class HelperTest { @Test void works() { new Helper(); } }"
            )
        );
        MatcherAssert.assertThat(
            "a class used by a test of its own package must be accepted",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.empty()
        );
    }

    @Test
    void reportsClassMentionedOnlyByAnotherPackage() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Lonely.java",
            this.source("foo", "final class Lonely { }")
        ).withFile(
            "src/main/java/bar/Main.java",
            this.source(
                "bar", "public class Main { Object one() { return Lonely.class; } }"
            )
        );
        MatcherAssert.assertThat(
            "a mention from another package must not count as a usage",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.hasSize(1)
        );
    }

    @Test
    void ignoresExcludedFile() throws IOException {
        final Environment env = new ExcludingEnvironment(
            new Environment.Mock().withFile(
                "src/main/java/foo/Lonely.java",
                this.source("foo", "final class Lonely { }")
            )
        );
        MatcherAssert.assertThat(
            "a class of an excluded file must be accepted",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.empty()
        );
    }

    @Test
    void ignoresFileThatCannotBeParsed() throws IOException {
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Broken.java",
            this.source("foo", "final class Broken { oops")
        );
        MatcherAssert.assertThat(
            "a file that does not parse must be left alone",
            new UnusedPackagePrivateClasses(env).validate(env.files("*.java")),
            Matchers.empty()
        );
    }

    private String source(final String pack, final String... lines) {
        return String.join(
            System.lineSeparator(),
            String.format("package %s;", pack),
            String.join(System.lineSeparator(), lines),
            ""
        );
    }
}
