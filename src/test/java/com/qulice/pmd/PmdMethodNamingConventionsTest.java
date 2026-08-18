/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link PmdValidator}'s rejection of unicode
 * characters inside method names under the
 * {@code MethodNamingConventions} rule.
 * @since 0.25.1
 */
final class PmdMethodNamingConventionsTest {

    @ParameterizedTest
    @ValueSource(strings = {"JnaLibraryNames.java", "JnaDirectNames.java"})
    void allowsNativeNamesInJnaBinding(final String file) throws Exception {
        new PmdAssert(
            file,
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("MethodNamingConventions")
            )
        ).assertOk();
    }

    @Test
    void rejectsNativeNamesOutsideJna() throws Exception {
        new PmdAssert(
            "FakeLibraryNames.java",
            Matchers.is(false),
            Matchers.containsString("MethodNamingConventions")
        ).assertOk();
    }

    @Test
    void prohibitsUnicodeCharactersInMethodNames() throws Exception {
        new PmdAssert(
            "UnicodeCharactersInMethodNames.java",
            Matchers.is(false),
            Matchers.containsString("MethodNamingConventions")
        ).assertOk();
    }
}
