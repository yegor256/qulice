/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.InputSource;

/**
 * Integration test case for all checkstyle checks.
 * @since 0.3
 */
final class ChecksTest {

    /**
     * Test checkstyle for true negative.
     * @param dir Directory where test scripts are located.
     * @throws Exception If something goes wrong
     */
    @ParameterizedTest
    @MethodSource("checks")
    @SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
    void testCheckstyleTruePositive(final String dir) throws Exception {
        final Collector collector = new ChecksTest.Collector();
        final AuditListener listener = new FakeAuditListener(collector);
        this.check(dir, "/Invalid.java", listener);
        final String[] violations = IOUtils.toString(
            Objects.requireNonNull(
                this.getClass().getResourceAsStream(
                    String.format("%s/violations.txt", dir)
                )
            ),
            StandardCharsets.UTF_8
        ).split("\n");
        for (final String line : violations) {
            final String[] sectors = line.split(":");
            final Integer pos = Integer.valueOf(sectors[0]);
            final String needle = sectors[1].trim();
            MatcherAssert.assertThat(
                collector.has(pos, needle),
                Matchers.describedAs(
                    String.format(
                        "Line no.%d ('%s') not reported by %s: '%s'",
                        pos,
                        needle,
                        dir,
                        collector.summary()
                    ),
                    Matchers.is(true)
                )
            );
        }
        MatcherAssert.assertThat(
            collector.eventCount(),
            Matchers.describedAs(
                String.format(
                    "Got more violations that expected for directory %s (%s)",
                    dir, collector.summary()
                ),
                Matchers.equalTo(violations.length)
            )
        );
    }

    /**
     * Test checkstyle for true negative.
     * @param dir Directory where test scripts are located.
     * @throws Exception If something goes wrong
     */
    @ParameterizedTest
    @MethodSource("checks")
    void testCheckstyleTrueNegative(final String dir) throws Exception {
        final Collector collector = new ChecksTest.Collector();
        final AuditListener listener = new FakeAuditListener(collector);
        this.check(dir, "/Valid.java", listener);
        MatcherAssert.assertThat(
            "Log should be empty for valid files",
            collector.summary(),
            Matchers.equalTo("")
        );
    }

    /**
     * Check one file.
     * @param dir Directory where test scripts are located.
     * @param name The name of the check
     * @param listener The listener
     * @throws Exception If something goes wrong inside
     */
    private void check(
        final String dir, final String name, final AuditListener listener
    ) throws Exception {
        final Checker checker = new Checker();
        final InputSource src = new InputSource(
            this.getClass().getResourceAsStream(
                String.format("%s/config.xml", dir)
            )
        );
        checker.setModuleClassLoader(
            Thread.currentThread().getContextClassLoader()
        );
        checker.configure(
            ConfigurationLoader.loadConfiguration(
                src,
                new PropertiesExpander(new Properties()),
                ConfigurationLoader.IgnoredModulesOptions.OMIT
            )
        );
        final List<File> files = new ArrayList<>(0);
        files.add(
            new File(
                this.getClass().getResource(
                    String.format("%s%s", dir, name)
                ).getFile()
            )
        );
        checker.addListener(listener);
        checker.process(files);
        checker.destroy();
    }

    /**
     * Returns full list of checks.
     * @return The list
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<String> checks() {
        return Stream.of(
            "MethodsOrderCheck",
            "MultilineJavadocTagsCheck",
            "StringLiteralsConcatenationCheck",
            "EmptyLinesCheck",
            "ImportCohesionCheck",
            "BracketsStructureCheck",
            "CurlyBracketsStructureCheck",
            "JavadocLocationCheck",
            "JavadocParameterOrderCheck",
            "MethodBodyCommentsCheck",
            "RequireThisCheck",
            "ProtectedMethodInFinalClassCheck",
            "NoJavadocForOverriddenMethodsCheck",
            "NonStaticMethodCheck",
            "ConstantUsageCheck",
            "JavadocEmptyLineCheck",
            "JavadocTagsCheck",
            "ProhibitNonFinalClassesCheck",
            "QualifyInnerClassCheck",
            "CommentCheck",
            "ProhibitUnusedPrivateConstructorCheck"
        ).map(s -> String.format("ChecksTest/%s", s));
    }

    /**
     * Mocked collector of checkstyle events.
     *
     * @since 0.1
     */
    private static final class Collector {
        /**
         * List of events received.
         */
        private final List<AuditEvent> events = new LinkedList<>();

        public void add(final AuditEvent event) {
            this.events.add(event);
        }

        /**
         * How many messages do we have?
         * @return Amount of messages reported
         */
        public int eventCount() {
            return this.events.size();
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
            return new Joined("; ", msgs).toString();
        }
    }

    /**
     * Fake Audit Listener.
     *
     * Just to set an event on addError() to a mocked Collector.
     *
     * @since 0.24.1
     */
    private static final class FakeAuditListener implements AuditListener {
        /**
         * Mocked collector.
         */
        private final ChecksTest.Collector collector;

        FakeAuditListener(final ChecksTest.Collector collect) {
            this.collector = collect;
        }

        @Override
        public void auditStarted(final AuditEvent event) {
            // Intentionally left blank
        }

        @Override
        public void auditFinished(final AuditEvent event) {
            // Intentionally left blank
        }

        @Override
        public void fileStarted(final AuditEvent event) {
            // Intentionally left blank
        }

        @Override
        public void fileFinished(final AuditEvent event) {
            // Intentionally left blank
        }

        @Override
        public void addError(final AuditEvent event) {
            this.collector.add(event);
        }

        @Override
        public void addException(
            final AuditEvent event,
            final Throwable throwable
        ) {
            // Intentionally left blank
        }
    }
}
