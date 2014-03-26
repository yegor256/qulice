/**
 * Copyright (c) 2011-2013, Qulice.com
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
package com.qulice.codenarc;

import com.google.common.collect.Lists;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.NullAppender;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link CodeNarcValidator} class.
 * @author Pavlo Shamrai (pshamrai@gmail.com)
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class CodeNarcValidatorTest {

    /**
     * CodeNarcValidator can throw ValidationExctpion when source code contains
     * some Groovy scripts that violate static analysis rules.
     * @throws Exception If something wrong happens inside.
     */
    @Test(expected = ValidationException.class)
    public void failsOnIncorrectGroovySources() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile("src/Main.groovy", "System.out.println('hi')");
        final Validator validator = new CodeNarcValidator();
        validator.validate(env);
    }

    /**
     * CodeNarcValidator can pass valid Groovy sources without any exceptions.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void passesCorrectFilesWithoutExceptions() throws Exception {
        final Validator validator = new CodeNarcValidator();
        final Environment env = new Environment.Mock()
            .withFile("src/foo/Foo.groovy", "// empty");
        validator.validate(env);
    }

    /**
     * CodeNarcValidator can report full names of files that contain
     * violations.
     * @throws Exception If error message does not include filename.
     */
    @Test(expected = ValidationException.class)
    public void reportsFullFileNamesOfGroovyScripts() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile("src/main/Foo.groovy", "System.out.println('foo')");
        final Validator validator = new CodeNarcValidator();
        final CodeNarcAppender appender = new CodeNarcAppender();
        org.apache.log4j.Logger.getRootLogger().addAppender(appender);
        try {
            validator.validate(env);
        } catch (ValidationException ex) {
            final List<String> messages = appender.getMessages();
            final Pattern pattern = Pattern.compile(
                "[a-zA-Z0-9_/]+\\.groovy\\[\\d+\\]: .*"
            );
            for (final String message : messages) {
                if (message.startsWith("CodeNarc validated ")) {
                    continue;
                }
                MatcherAssert.assertThat(
                    pattern.matcher(message).matches(),
                    Matchers.describedAs(message, Matchers.is(true))
                );
            }
            throw ex;
        } finally {
            org.apache.log4j.Logger.getRootLogger().removeAppender(appender);
        }
    }

    /**
     * Appender for log verifying.
     */
    private static final class CodeNarcAppender extends NullAppender {
        /**
         * List of logged messages.
         */
        private final transient Collection<String> messages =
            new ConcurrentLinkedQueue<String>();
        @Override
        public void doAppend(final LoggingEvent event) {
            this.messages.add(event.getMessage().toString());
        }
        /**
         * Get list of logged messages.
         * @return The list of logged messages
         */
        public List<String> getMessages() {
            return Collections.unmodifiableList(
                Lists.newArrayList(this.messages)
            );
        }
    }

}
