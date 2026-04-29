/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.google.common.base.Joiner;
import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * Test case for general {@link CheckstyleValidator} behavior that
 * is not tied to a single check: method references, record/default
 * methods, lambda and generics at end of line, chained lambdas
 * passed as arguments, literal comparisons, and the imports-only
 * regression guard. Per-check coverage lives in the dedicated
 * {@code *CheckTest.java} and {@code Checkstyle*Test.java} files
 * in this package.
 * @since 0.3
 */
final class CheckstyleValidatorTest {

    @Test
    void acceptsInstanceMethodReferences() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("InstanceMethodRef.java", true)
        );
    }

    @Test
    void acceptsIdAsMethodName() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("IdMethodName.java", true)
        );
    }

    @Test
    void acceptsDefaultMethodsWithFinalModifiers() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("DefaultMethods.java", true)
        );
    }

    @Test
    void supportsRecordTypes() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidRecord.java", true)
        );
    }

    @Test
    void acceptsLambdaAndGenericsAtEndOfLine() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidLambdaAndGenericsAtEndOfLine.java", true)
        );
    }

    @Test
    void acceptsChainedLambdaAsArgument() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidLambdaIndentation.java", true)
        );
    }

    @Test
    void allowsStringLiteralsOnBothSideInComparisons() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidLiteralComparisonCheck.java", true)
        );
    }

    @Test
    void failsClearlyWhenCacheParentIsNotWritable() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File parent = new File(mock.tempdir(), "checkstyle");
        Assumptions.assumeTrue(
            parent.mkdirs() || parent.isDirectory(),
            "Parent directory must exist for the regression scenario"
        );
        try {
            Assumptions.assumeTrue(
                parent.setReadOnly(),
                "Filesystem does not support marking a directory read-only"
            );
            Assumptions.assumeFalse(
                parent.canWrite(),
                "Skipped: current user can write despite read-only attribute"
            );
            final Environment env = mock.withFile(
                "src/main/java/foo/Foo.java",
                "package foo; class Foo {}"
            );
            MatcherAssert.assertThat(
                "Validation must fail fast with a clear writability error",
                Assertions.assertThrows(
                    IllegalStateException.class,
                    () -> new CheckstyleValidator(env).validate(env.files("Foo.java"))
                ).getMessage(),
                Matchers.allOf(
                    Matchers.containsString("write"),
                    Matchers.containsString(parent.getAbsolutePath())
                )
            );
        } finally {
            parent.setWritable(true, false);
        }
    }

    @Test
    void doesNotThrowExceptionIfImportsOnly() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final String crlf = String.valueOf('\r').concat(String.valueOf('\n'));
        final String name = "Foo.java";
        final Environment env = mock.withParam(
            "license",
            String.format(
                "file:%s",
                new License().savePackageInfo(
                    new File(mock.basedir(), "src/main/java/foo")
                ).withLines("License-1.", "", "License-2.")
                    .withEol(String.valueOf('\n'))
                    .file()
            )
        ).withFile(
            String.format("src/main/java/foo/%s", name),
            Joiner.on(crlf).join(
                "package com.google;",
                crlf,
                "import java.util.*;"
            )
        );
        MatcherAssert.assertThat(
            "Validation error should exist",
            new CheckstyleValidator(env).validate(env.files(name)),
            Matchers.not(Matchers.<Violation>empty())
        );
    }

    private Collection<Violation> runValidation(final String file,
        final boolean passes) throws IOException {
        final Environment.Mock mock = new Environment.Mock();
        final Environment env = mock.withParam(
            "license",
            String.format(
                "file:%s",
                new License().savePackageInfo(
                    new File(mock.basedir(), "src/main/java/foo")
                ).withLines("Hello.")
                    .withEol(String.valueOf('\n')).file()
            )
        ).withFile(
            String.format("src/main/java/foo/%s", file),
            new IoCheckedText(
                new TextOf(
                    new ResourceOf(
                        new FormattedText("com/qulice/checkstyle/%s", file)
                    )
                )
            ).asString()
        );
        final Collection<Violation> results =
            new CheckstyleValidator(env).validate(env.files(file));
        MatcherAssert.assertThat(
            "validation result should match expected state",
            results.isEmpty(),
            Matchers.is(passes)
        );
        return results;
    }
}
