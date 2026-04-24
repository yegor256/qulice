/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator}'s rule that forbids
 * {@code Files.createFile} inside tests but allows it elsewhere.
 * @since 0.25.1
 */
final class PmdFilesCreateFileTest {

    /**
     * Error message for the forbidden Files.createFile usage in tests.
     */
    private static final String FILES_CREATE_ERR =
        "Files.createFile should not be used in tests, replace them with @Rule TemporaryFolder";

    @Test
    void forbidsFilesCreateFileInTests() throws Exception {
        new PmdAssert(
            "FilesCreateFileTest.java",
            Matchers.is(false),
            Matchers.containsString(
                PmdFilesCreateFileTest.FILES_CREATE_ERR
            )
        ).assertOk();
    }

    @Test
    void allowsFilesCreateFileOutsideOfTests() throws Exception {
        new PmdAssert(
            "FilesCreateFileOther.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString(
                    PmdFilesCreateFileTest.FILES_CREATE_ERR
                )
            )
        ).assertOk();
    }
}
