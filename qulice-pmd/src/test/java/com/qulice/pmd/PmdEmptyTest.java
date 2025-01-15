/*
 * Copyright (c) 2011-2025 Yegor Bugayenko
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
