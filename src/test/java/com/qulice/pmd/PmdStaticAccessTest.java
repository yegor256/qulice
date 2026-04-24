/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.jcabi.matchers.RegexMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s rule that static fields and
 * methods must be accessed via the class name, not via an instance
 * or {@code this}.
 * @since 0.25.1
 */
final class PmdStaticAccessTest {

    /**
     * Pattern: static field accessed directly or via instance.
     */
    private static final String STATIC_ACCESS =
        "%s\\[\\d+-\\d+\\]: Static fields should be accessed in a static way \\[CLASS_NAME.FIELD_NAME\\]\\.";

    /**
     * Pattern: static member accessed via {@code this} reference.
     */
    private static final String STATIC_VIA_THIS =
        "%s\\[\\d+-\\d+\\]: Static members should be accessed in a static way \\[CLASS_NAME.FIELD_NAME\\], not via instance reference.";

    @Test
    void acceptsCallToStaticFieldsInStaticWay() throws Exception {
        final String file = "StaticAccessToStaticFields.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.allOf(
                Matchers.not(
                    RegexMatchers.containsPattern(
                        String.format(PmdStaticAccessTest.STATIC_ACCESS, file)
                    )
                ),
                Matchers.not(
                    RegexMatchers.containsPattern(
                        String.format(PmdStaticAccessTest.STATIC_VIA_THIS, file)
                    )
                )
            )
        ).assertOk();
    }

    @Test
    void acceptsDirectAccessToStaticFieldInStaticInitializer() throws Exception {
        final String file = "StaticInitializerAssignsFinalField.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PmdStaticAccessTest.STATIC_ACCESS, file)
                )
            )
        ).assertOk();
    }

    @Test
    void forbidsCallToStaticFieldsDirectly() throws Exception {
        final String file = "DirectAccessToStaticFields.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdStaticAccessTest.STATIC_ACCESS, file)
            )
        ).assertOk();
    }

    @Test
    void forbidsCallToStaticFieldsViaThis() throws Exception {
        final String file = "AccessToStaticFieldsViaThis.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdStaticAccessTest.STATIC_VIA_THIS, file)
            )
        ).assertOk();
    }

    @Test
    void forbidsCallToStaticMethodsViaThis() throws Exception {
        final String file = "AccessToStaticMethodsViaThis.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdStaticAccessTest.STATIC_VIA_THIS, file)
            )
        ).assertOk();
    }
}
