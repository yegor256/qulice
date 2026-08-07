/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for
 * {@link com.qulice.pmd.rules.ImplicitFunctionalInterfaceRule}.
 * @since 1.0
 */
final class ImplicitFunctionalInterfaceRuleTest {

    /**
     * Name of the rule as it appears in violation reports.
     */
    private static final String RULE = "(ImplicitFunctionalInterface)";

    @Test
    void flagsInterfaceWithSingleAbstractMethod() throws Exception {
        new PmdAssert(
            "ImplicitFunctionalInterfaceSingle.java",
            new IsEqual<>(false),
            Matchers.containsString(
                ImplicitFunctionalInterfaceRuleTest.RULE
            )
        ).assertOk();
    }

    @Test
    void flagsInterfaceExtendingResolvedMarker() throws Exception {
        new PmdAssert(
            "ImplicitFunctionalInterfaceMarkerParent.java",
            new IsEqual<>(false),
            Matchers.containsString(
                ImplicitFunctionalInterfaceRuleTest.RULE
            )
        ).assertOk();
    }

    @Test
    void ignoresInterfaceExtendingUnresolvedParents() throws Exception {
        new PmdAssert(
            "ImplicitFunctionalInterfaceUnresolvedParent.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString(
                    ImplicitFunctionalInterfaceRuleTest.RULE
                )
            )
        ).assertOk();
    }

    @Test
    void ignoresInterfaceExtendingResolvedParentWithMoreMethods()
        throws Exception {
        new PmdAssert(
            "ImplicitFunctionalInterfaceJdkParent.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString(
                    ImplicitFunctionalInterfaceRuleTest.RULE
                )
            )
        ).assertOk();
    }
}
