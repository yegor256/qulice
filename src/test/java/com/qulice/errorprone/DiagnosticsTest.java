/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.qulice.spi.Violation;
import java.util.Collection;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Diagnostics}.
 * @since 1.0
 */
final class DiagnosticsTest {

    @Test
    void namesTheErrorProneCheck() {
        MatcherAssert.assertThat(
            "bracketed check name must survive into the violation",
            new Diagnostics("ErrorProne", "/prj").violations(
                List.of("/prj/Foo.java:42: warning: [SelfAssignment] assigned to itself")
            ).iterator().next().name(),
            Matchers.equalTo("SelfAssignment")
        );
    }

    @Test
    void namesThePlainCompilerAsCheck() {
        MatcherAssert.assertThat(
            "an unlabelled diagnostic cannot be left nameless",
            new Diagnostics("ErrorProne", "/prj").violations(
                List.of("/prj/Foo.java:7: error: cannot find symbol")
            ).iterator().next().name(),
            Matchers.equalTo("javac")
        );
    }

    @Test
    void keepsTheSourcePosition() {
        MatcherAssert.assertThat(
            "line number of the diagnostic must not be lost",
            new Diagnostics("ErrorProne", "/prj").violations(
                List.of("/prj/Foo.java:19: warning: [removal] old() is deprecated")
            ).iterator().next().lines(),
            Matchers.equalTo("19")
        );
    }

    @Test
    void readsWindowsDriveLetterPath() {
        MatcherAssert.assertThat(
            "a drive letter cannot be mistaken for the line separator",
            new Diagnostics("ErrorProne", "C:\\prj").violations(
                List.of("C:\\prj\\Foo.java:7: error: cannot find symbol")
            ).iterator().next().file(),
            Matchers.equalTo("C:\\prj\\Foo.java")
        );
    }

    @Test
    void blamesProjectForPositionlessDiagnostic() {
        MatcherAssert.assertThat(
            "a diagnostic with no source position must still land somewhere",
            new Diagnostics("ErrorProne", "/prj").violations(
                List.of("error: warnings found and -Werror specified")
            ).iterator().next().file(),
            Matchers.equalTo("/prj")
        );
    }

    @Test
    void dontReportCompilerChatter() {
        final Collection<Violation> violations =
            new Diagnostics("ErrorProne", "/prj").violations(
                List.of(
                    "    int count() { return Missing.value(); }",
                    "                         ^",
                    "  symbol:   variable Missing",
                    "  location: class Broken",
                    "Note: Some input files use unchecked operations.",
                    "1 error",
                    "5 warnings"
                )
            );
        MatcherAssert.assertThat(
            String.format("carets and counters are not violations: %s", violations),
            violations,
            Matchers.<Violation>empty()
        );
    }
}
