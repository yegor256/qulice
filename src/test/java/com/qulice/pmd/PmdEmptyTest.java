/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PmdValidator} class.
 * @since 0.15
 */
@SuppressWarnings("PMD.TooManyMethods")
final class PmdEmptyTest {
    /**
     * Makes sure that empty static initializers fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptyStaticInitializer() throws Exception {
        new PmdAssert(
            "EmptyStaticInitializer.java",
            Matchers.is(false),
            Matchers.containsString(
                "Empty initializer statement (EmptyControlStatement)"
            )
        ).validate();
    }

    /**
     * Makes sure that empty statement blocks fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptyStatementBlock() throws Exception {
        new PmdAssert(
            "EmptyStatementBlock.java",
            Matchers.is(false),
            Matchers.containsString(
                "Empty block "
            )
        ).validate();
    }

    /**
     * Makes sure that empty initializers fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptyInitializer() throws Exception {
        new PmdAssert(
            "EmptyInitializer.java",
            Matchers.is(false),
            Matchers.containsString(
                "Empty initializer statement "
            )
        ).validate();
    }

    /**
     * Makes sure that empty statement not in a loop fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptyNonLoopStatement() throws Exception {
        new PmdAssert(
            "EmptyStatementNotInLoop.java",
            Matchers.is(false),
            Matchers.containsString(
                "Unnecessary semicolon "
            )
        ).validate();
    }

    /**
     * Makes sure that empty synchronized statements fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptySynchronizedBlock() throws Exception {
        new PmdAssert(
            "EmptySynchronizedBlock.java",
            Matchers.is(false),
            Matchers.containsString(
                "Empty synchronized statement "
            )
        ).validate();
    }

    /**
     * Makes sure that empty switch statements fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptySwitchStatement() throws Exception {
        new PmdAssert(
            "EmptySwitchStmt.java",
            Matchers.is(false),
            Matchers.containsString(
                "Empty switch statement "
            )
        ).validate();
    }

    /**
     * Makes sure that empty finally blocks fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptyFinallyBlock() throws Exception {
        new PmdAssert(
            "EmptyFinallyBlock.java",
            Matchers.is(false),
            Matchers.containsString("Empty finally clause")
        ).validate();
    }

    /**
     * Makes sure that empty while statements fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptyWhileStatement() throws Exception {
        new PmdAssert(
            "EmptyWhileStmt.java",
            Matchers.is(false),
            Matchers.containsString("Empty while statement ")
        ).validate();
    }

    /**
     * Makes sure that empty if blocks fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptyIfStatement() throws Exception {
        new PmdAssert(
            "EmptyIfStmt.java",
            Matchers.is(false),
            Matchers.containsString("Empty if statement ")
        ).validate();
    }

    /**
     * Makes sure that empty catch blocks fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    void failsForEmptyCatchBlock() throws Exception {
        new PmdAssert(
            "EmptyCatchBlock.java",
            Matchers.is(false),
            Matchers.containsString("Avoid empty catch blocks")
        ).validate();
    }
}
