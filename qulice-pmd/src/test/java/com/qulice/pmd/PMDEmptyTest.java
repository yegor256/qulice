/**
 * Copyright (c) 2011-2015, Qulice.com
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

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link PMDValidator} class.
 * @author Prahlad Yeri (prahladyeri@yahoo.com)
 * @version $Id$
 * @todo #544:30min Tests below pass only when run sequentially, when they are
 * run in parallel some of them start to fail. Please fix the tests below and
 * remove override of maven-surefire-plugin configuration in qulice-pmd pom.xml.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class PMDEmptyTest {
    /**
     * Makes sure that empty static initializers fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptyStaticInitializer() throws Exception {
        this.validatePMD(
            "EmptyStaticInitializer.java",
            Matchers.containsString(
                "Empty static initializer was found"
            )
        );
    }

    /**
     * Makes sure that empty statement blocks fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptyStatementBlock() throws Exception {
        this.validatePMD(
            "EmptyStatementBlock.java",
            Matchers.containsString(
                "Avoid empty block statements"
            )
        );
    }

    /**
     * Makes sure that empty initializers fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptyInitializer() throws Exception {
        this.validatePMD(
            "EmptyInitializer.java",
            Matchers.containsString(
                "Empty initializer was found"
            )
        );
    }

    /**
     * Makes sure that empty statement not in a loop fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptyNonLoopStatement() throws Exception {
        this.validatePMD(
            "EmptyStatementNotInLoop.java",
            Matchers.containsString(
                "An empty statement (semicolon) not part of a loop"
            )
        );
    }

    /**
     * Makes sure that empty synchronized statements fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptySynchronizedBlock() throws Exception {
        this.validatePMD(
            "EmptySynchronizedBlock.java",
            Matchers.containsString(
                "Avoid empty synchronized blocks"
            )
        );
    }

    /**
     * Makes sure that empty switch statements fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptySwitchStatement() throws Exception {
        this.validatePMD(
            "EmptySwitchStmt.java",
            Matchers.containsString(
                "Avoid empty switch statements"
            )
        );
    }

    /**
     * Makes sure that empty finally blocks fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptyFinallyBlock() throws Exception {
        this.validatePMD(
            "EmptyFinallyBlock.java",
            Matchers.containsString("Avoid empty finally blocks")
        );
    }

    /**
     * Makes sure that empty while statements fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptyWhileStatement() throws Exception {
        this.validatePMD(
            "EmptyWhileStmt.java",
            Matchers.containsString("Avoid empty while statements")
        );
    }

    /**
     * Makes sure that empty if blocks fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptyIfStatement() throws Exception {
        this.validatePMD(
            "EmptyIfStmt.java",
            Matchers.containsString("Avoid empty if statements")
        );
    }

    /**
     * Makes sure that empty catch blocks fail with an error.
     * @throws Exception when something goes wrong
     */
    @Test
    public void failsForEmptyCatchBlock() throws Exception {
        this.validatePMD(
            "EmptyCatchBlock.java",
            Matchers.containsString("Avoid empty catch blocks")
        );
    }

    /**
     * Validates that PMD reported given violation.
     * @param file String containing file name to emulate.
     * @param matcher Matching string needed in the output.
     * @throws Exception If something wrong happens inside.
     */
    private void validatePMD(final String file,
        final Matcher<String> matcher) throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final StringWriter writer = new StringWriter();
        final WriterAppender appender =
            new WriterAppender(new SimpleLayout(), writer);
        try {
            Logger.getRootLogger().addAppender(
                appender
            );
            final Environment env = mock.withFile(
                String.format("src/main/java/emp/%s", file),
                IOUtils.toString(
                    this.getClass().getResourceAsStream(file)
                )
            );
            boolean thrown = false;
            try {
                new PMDValidator().validate(env);
            } catch (final ValidationException ex) {
                thrown = true;
            }
            writer.flush();
            MatcherAssert.assertThat(thrown, Matchers.is(true));
            MatcherAssert.assertThat(writer.toString(), matcher);
        } finally {
            Logger.getRootLogger().removeAppender(appender);
        }
    }
}
