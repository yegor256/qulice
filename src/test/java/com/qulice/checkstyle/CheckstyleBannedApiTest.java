/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CheckstyleValidator}'s detection of banned
 * API usages such as Guava {@code Lists.newArrayList()} with no
 * size, Apache Commons {@code CharEncoding}, and interface type
 * parameter naming.
 *
 * @since 0.25.1
 */
final class CheckstyleBannedApiTest {

    @Test
    void reportsGuavaNewArrayListWithoutSize() throws Exception {
        final String file = "GuavaNewArrayList.java";
        MatcherAssert.assertThat(
            "Guava Lists.newArrayList without size must be reported",
            this.runValidation(file, false),
            Matchers.hasItem(
                new ViolationMatcher(
                    "Lists.newArrayList should be initialized with a size parameter",
                    file
                )
            )
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void reportsAllCharEncodingUsages() throws Exception {
        final String message =
            "Use java.nio.charset.StandardCharsets instead";
        final String file = "DoNotUseCharEncoding.java";
        final Collection<Violation> results = this.runValidation(
            file, false
        );
        final String name = "RegexpSinglelineCheck";
        MatcherAssert.assertThat(
            "8 violations should be found",
            results,
            new IsIterableContainingInOrder<>(
                new ListOf<>(
                    new ViolationMatcher(message, file, "6", name),
                    new ViolationMatcher(message, file, "7", name),
                    new ViolationMatcher(message, file, "8", name),
                    new ViolationMatcher(message, file, "9", name),
                    new ViolationMatcher(message, file, "23", name),
                    new ViolationMatcher(message, file, "24", name),
                    new ViolationMatcher(message, file, "25", name),
                    new ViolationMatcher(message, file, "26", name)
                )
            )
        );
    }

    @Test
    void reportsInvalidInterfaceTypeParameterName() throws Exception {
        final String file = "InterfaceTypeParameterName.java";
        MatcherAssert.assertThat(
            "Interface type parameter violation must be reported",
            this.runValidation(file, false),
            Matchers.hasItem(
                new ViolationMatcher(
                    "Name 'wRoNg' must match pattern", file,
                    "11", "InterfaceTypeParameterNameCheck"
                )
            )
        );
    }

    @SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
    private Collection<Violation> runValidation(final String file,
        final boolean passes) throws IOException {
        final Environment.Mock mock = new Environment.Mock();
        final File license = new License().savePackageInfo(
            new File(mock.basedir(), "src/main/java/foo")
        ).withLines("Hello.")
            .withEol("\n").file();
        final Environment env = mock.withParam(
            "license",
            String.format("file:%s", license)
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
        if (passes) {
            MatcherAssert.assertThat(
                results,
                Matchers.<Violation>empty()
            );
        } else {
            MatcherAssert.assertThat(
                results,
                Matchers.not(Matchers.<Violation>empty())
            );
        }
        return results;
    }
}
