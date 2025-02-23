/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;

/**
 * Test case for LocalVariableCouldBeFinal.
 *
 * @since 0.18
 */
final class UnusedImportsRuleTest {

    /**
     * UnusedImport can detect when the class has an unused import line and
     * show error message correctly.
     *
     * @throws Exception If something goes wrong
     */
    @Test
    void detectUnusedImportLine() throws Exception {
        new PmdAssert(
            "UnusedImports.java",
            new IsEqual<>(false),
            new StringStartsWith(
                String.join(
                    " ",
                    "PMD: UnusedImports.java[33-33]: Unused import",
                    "'unused.bar.foo.UnusedImport'",
                    "(UnnecessaryImport)"
                ).trim()
            )
        ).validate();
    }
}
