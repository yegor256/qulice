/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Xplugin}.
 * @since 1.0
 */
final class XpluginTest {

    @Test
    void switchesQuliceOwnPatternsOff() {
        MatcherAssert.assertThat(
            "the patterns Qulice disables must always be disabled",
            new Xplugin("").argument(),
            Matchers.equalTo(
                String.join(
                    " ",
                    "-Xplugin:ErrorProne",
                    "-Xep:InvalidBlockTag:OFF",
                    "-Xep:OperatorPrecedence:OFF",
                    "-Xep:UnicodeInCode:OFF"
                )
            )
        );
    }

    @Test
    void appendsFlagsOfProjectLast() {
        MatcherAssert.assertThat(
            "the project must have the last word on every pattern",
            new Xplugin("-Xep:OperatorPrecedence:ERROR").argument(),
            Matchers.endsWith(
                "-Xep:UnicodeInCode:OFF -Xep:OperatorPrecedence:ERROR"
            )
        );
    }

    @Test
    void takesFlagsSeparatedByCommas() {
        MatcherAssert.assertThat(
            "commas must separate flags just like spaces do",
            new Xplugin(" -Xep:UnusedVariable:OFF, -Xep:SelfAssignment:OFF ")
                .argument(),
            Matchers.endsWith(
                "-Xep:UnusedVariable:OFF -Xep:SelfAssignment:OFF"
            )
        );
    }

    @Test
    void refusesFlagErrorProneWouldNotUnderstand() {
        MatcherAssert.assertThat(
            "the flag that cannot work must be named in the failure",
            Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Xplugin("-Xlint:all").argument(),
                "a flag that is not an ErrorProne one must be refused"
            ).getMessage(),
            Matchers.containsString("-Xlint:all")
        );
    }
}
