/**
 * Copyright (c) 2011-2018, Qulice.com
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
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xml.sax.InputSource;

/**
 * Integration test case for all checkstyle checks.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.3
 */
@RunWith(Parameterized.class)
public final class ChecksTest {

    /**
     * Directories where test scripts are located.
     */
    private static final String[] CHECKS = {
        "MethodsOrderCheck",
        "MultilineJavadocTagsCheck",
        "StringLiteralsConcatenationCheck",
        "EmptyLinesCheck",
        "ImportCohesionCheck",
        "JavadocTagsCheck",
        "BracketsStructureCheck",
        "CurlyBracketsStructureCheck",
        "JavadocLocationCheck",
        "MethodBodyCommentsCheck",
        "RequireThisCheck",
        "ProtectedMethodInFinalClassCheck",
        "NoJavadocForOverriddenMethodsCheck",
        "NonStaticMethodCheck",
        "ConstantUsageCheck",
        "JavadocEmptyLineCheck",
    };

    /**
     * Current directory we're working with.
     */
    private final String dir;

    /**
     * Public ctor.
     * @param name The name of the check to work with
     */
    public ChecksTest(final String name) {
        this.dir = String.format("ChecksTest/%s", name);
    }

    /**
     * Returns full list of checks.
     * @return The list
     */
    @Parameterized.Parameters
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public static Collection<Object[]> dirs() {
        final Collection<Object[]> dirs = new LinkedList<>();
        for (final String url : ChecksTest.CHECKS) {
            dirs.add(new Object[] {url});
        }
        return dirs;
    }

    /**
     * Test checkstyle for true positive.
     * @throws Exception If something goes wrong
     */
    @Test
    public void testCheckstyleTruePositive() throws Exception {
        final AuditListener listener = Mockito.mock(AuditListener.class);
        final Collector collector = new ChecksTest.Collector();
        Mockito.doAnswer(collector).when(listener)
            .addError(Mockito.any(AuditEvent.class));
        this.check("/Invalid.java", listener);
        final String[] violations = StringUtils.split(
            IOUtils.toString(
                this.getClass().getResourceAsStream(
                    String.format("%s/violations.txt", this.dir)
                )
            ),
            "\n"
        );
        for (final String line : violations) {
            final String[] sectors = StringUtils.split(line, ":");
            final Integer pos = Integer.valueOf(sectors[0]);
            final String needle = sectors[1].trim();
            MatcherAssert.assertThat(
                collector.has(pos, needle),
                Matchers.describedAs(
                    String.format(
                        "Line no.%d ('%s') not reported by %s: '%s'",
                        pos,
                        needle,
                        this.dir,
                        collector.summary()
                    ),
                    Matchers.is(true)
                )
            );
        }
    }

    /**
     * Test checkstyle for true negative.
     * @throws Exception If something goes wrong
     */
    @Test
    public void testCheckstyleTrueNegative() throws Exception {
        final AuditListener listener = Mockito.mock(AuditListener.class);
        final Collector collector = new ChecksTest.Collector();
        Mockito.doAnswer(collector).when(listener)
            .addError(Mockito.any(AuditEvent.class));
        this.check("/Valid.java", listener);
        MatcherAssert.assertThat(collector.summary(), Matchers.equalTo(""));
        Mockito.verify(listener, Mockito.times(0))
            .addError(Mockito.any(AuditEvent.class));
    }

    /**
     * Check one file.
     * @param name The name of the check
     * @param listener The listener
     * @throws Exception If something goes wrong inside
     */
    private void check(final String name, final AuditListener listener)
        throws Exception {
        final Checker checker = new Checker();
        final InputSource src = new InputSource(
            this.getClass().getResourceAsStream(
                String.format("%s/config.xml", this.dir)
            )
        );
        checker.setClassLoader(Thread.currentThread().getContextClassLoader());
        checker.setModuleClassLoader(
            Thread.currentThread().getContextClassLoader()
        );
        checker.configure(
            ConfigurationLoader.loadConfiguration(
                src,
                new PropertiesExpander(new Properties()),
                true
            )
        );
        final List<File> files = new ArrayList<>(0);
        files.add(
            new File(
                this.getClass().getResource(
                    String.format("%s%s", this.dir, name)
                ).getFile()
            )
        );
        checker.addListener(listener);
        checker.process(files);
        checker.destroy();
    }

    /**
     * Mocked collector of checkstyle events.
     */
    private static class Collector implements Answer<Object> {
        /**
         * List of events received.
         */
        private final List<AuditEvent> events = new LinkedList<>();
        @Override
        public Object answer(final InvocationOnMock invocation) {
            this.events.add((AuditEvent) invocation.getArguments()[0]);
            return null;
        }
        /**
         * Do we have this message for this line?
         * @param line The number of the line
         * @param msg The message we're looking for
         * @return This message was reported for the give line?
         */
        public boolean has(final Integer line, final String msg) {
            boolean has = false;
            for (final AuditEvent event : this.events) {
                if (event.getLine() == line && event.getMessage().equals(msg)) {
                    has = true;
                    break;
                }
            }
            return has;
        }
        /**
         * Returns full summary.
         * @return The test summary of all events
         */
        public String summary() {
            final List<String> msgs = new LinkedList<>();
            for (final AuditEvent event : this.events) {
                msgs.add(
                    String.format(
                        "%s:%s",
                        event.getLine(),
                        event.getMessage()
                    )
                );
            }
            return StringUtils.join(msgs, "; ");
        }
    }

}
