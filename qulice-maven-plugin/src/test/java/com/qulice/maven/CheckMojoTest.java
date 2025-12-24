/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Validator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CheckMojo} class.
 * @since 0.3
 */
final class CheckMojoTest {

    /**
     * CheckMojo can skip execution if "skip" flag is set.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void skipsExecutionOnSkipFlag() throws Exception {
        final CheckMojo mojo = new CheckMojo();
        final Logger logger = new FakeLogger();
        final Log log = new DefaultLog(logger);
        mojo.setLog(log);
        mojo.setSkip(true);
        mojo.execute();
        Assertions.assertEquals("[INFO] Execution skipped", logger.toString());
    }

    /**
     * CheckMojo can set timeout to "forever".
     */
    @Test
    void setsTimeoutToForever() {
        final CheckMojo mojo = new CheckMojo();
        mojo.setTimeout("forever");
        final var validator = new BlockedValidator();
        final ValidatorsProvider provider = new ValidatorsProviderMocker()
            .withExternalResource(validator)
            .mock();
        mojo.setValidatorsProvider(provider);
        final MavenProject project = new MavenProject();
        mojo.setProject(project);
        mojo.setLog(new DefaultLog(new FakeLogger()));
        new Thread(
            () -> {
                try {
                    mojo.execute();
                } catch (final MojoFailureException exception) {
                    throw new IllegalStateException(exception);
                }
            }
        ).start();
        validator.await();
        Assertions.assertEquals(
            1,
            validator.count(),
            "Without the 'await' statement above, this test would run forever"
        );
    }

    /**
     * CheckMojo can set timeout to "1s".
     */
    @Test
    void setsTimeoutToOneSecond() {
        final CheckMojo mojo = new CheckMojo();
        mojo.setTimeout("1s");
        final ValidatorsProvider provider = new ValidatorsProviderMocker()
            .withExternalResource(new BlockedValidator())
            .mock();
        mojo.setValidatorsProvider(provider);
        final MavenProject project = new MavenProject();
        mojo.setProject(project);
        mojo.setLog(new DefaultLog(new FakeLogger()));
        Assertions.assertSame(
            TimeoutException.class,
            Assertions.assertThrows(
                IllegalStateException.class,
                mojo::execute,
                "Should throw IllegalStateException because of timeout"
            ).getCause().getClass(),
            "The cause is expected to be timeout"
        );
    }

    /**
     * CheckMojo can validate a project using all provided validators.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void validatesUsingAllProvidedValidators() throws Exception {
        final CheckMojo mojo = new CheckMojo();
        final FakeValidator external = new FakeValidator("somename");
        final FakeResourceValidator rexternal = new FakeResourceValidator(
            "other"
        );
        final FakeMavenValidator internal = new FakeMavenValidator();
        final ValidatorsProvider provider = new ValidatorsProviderMocker()
            .withInternal(internal)
            .withExternal(external)
            .withExternalResource(rexternal)
            .mock();
        mojo.setValidatorsProvider(provider);
        final MavenProject project = new MavenProject();
        mojo.setProject(project);
        mojo.setLog(new DefaultLog(new FakeLogger()));
        final Context context = new DefaultContext();
        mojo.contextualize(context);
        mojo.execute();
        Assertions.assertEquals(1, internal.count());
        Assertions.assertEquals(1, external.count());
        Assertions.assertEquals(1, rexternal.count());
    }

    /**
     * FakeLogger.
     * A logger that logs in a buffer.
     *
     * @since 0.24.1
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private static final class FakeLogger extends AbstractLogger {
        /**
         * Log level tags.
         */
        private static final String[] TAGS = {
            "[DEBUG] ",
            "[INFO] ",
            "[WARNING] ",
            "[ERROR] ",
            "[FATAL ERROR] ",
        };

        /**
         * Logged messages.
         */
        private final StringBuilder messages;

        FakeLogger() {
            this(1, "fakelogger");
        }

        FakeLogger(final int threshold, final String name) {
            super(threshold, name);
            this.messages = new StringBuilder();
        }

        @Override
        public void debug(final String message, final Throwable throwable) {
            if (this.isDebugEnabled()) {
                this.messages.append(FakeLogger.TAGS[0].concat(message));
                if (throwable != null) {
                    throwable.printStackTrace(System.out);
                }
            }
        }

        @Override
        public void info(final String message, final Throwable throwable) {
            if (this.isInfoEnabled()) {
                this.messages.append(FakeLogger.TAGS[1].concat(message));
                if (throwable != null) {
                    throwable.printStackTrace(System.out);
                }
            }
        }

        @Override
        public void warn(final String message, final Throwable throwable) {
            if (this.isWarnEnabled()) {
                this.messages.append(FakeLogger.TAGS[2].concat(message));
                if (throwable != null) {
                    throwable.printStackTrace(System.out);
                }
            }
        }

        @Override
        public void error(final String message, final Throwable throwable) {
            if (this.isErrorEnabled()) {
                this.messages.append(FakeLogger.TAGS[3].concat(message));
                if (throwable != null) {
                    throwable.printStackTrace(System.out);
                }
            }
        }

        @Override
        public void fatalError(
            final String message,
            final Throwable throwable
        ) {
            if (this.isFatalErrorEnabled()) {
                this.messages.append(FakeLogger.TAGS[4].concat(message));
                if (throwable != null) {
                    throwable.printStackTrace(System.out);
                }
            }
        }

        @Override
        public Logger getChildLogger(final String name) {
            return this;
        }

        @Override
        public String toString() {
            return this.messages.toString();
        }
    }

    /**
     * FakeValidator
     * A mock to a Validator.
     *
     * @since 0.24.1
     */
    private static final class FakeValidator implements Validator {
        /**
         * Validator name.
         */
        private final String label;

        /**
         * Method calls counter.
         */
        private final AtomicInteger cnt;

        FakeValidator(final String name) {
            this.label = name;
            this.cnt = new AtomicInteger(0);
        }

        @Override
        public void validate(final Environment env) {
            this.cnt.incrementAndGet();
        }

        @Override
        public String name() {
            return this.label;
        }

        public int count() {
            return this.cnt.get();
        }
    }

    /**
     * BlockedValidator
     * A mock to a Validator that blocks forever.
     *
     * @since 0.24.1
     */
    private static final class BlockedValidator implements ResourceValidator {

        /**
         * Method calls counter.
         */
        private final AtomicInteger cnt;

        /**
         * Latch to signal when validation starts.
         */
        private final CountDownLatch latch;

        BlockedValidator() {
            this.cnt = new AtomicInteger(0);
            this.latch = new CountDownLatch(1);
        }

        @Override
        public Collection<Violation> validate(final Collection<File> ignore) {
            this.cnt.incrementAndGet();
            this.latch.countDown();
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return Collections.emptyList();
        }

        @Override
        public String name() {
            return "blocked forever";
        }

        public int count() {
            return this.cnt.get();
        }

        public void await() {
            try {
                this.latch.await();
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * FakeResourceValidator.
     * A mock to a ResourceValidator.
     *
     * @since 0.24.1
     */
    private static final class FakeResourceValidator
        implements ResourceValidator {
        /**
         * Resource validator name.
         */
        private final String label;

        /**
         * Method calls counter.
         */
        private final AtomicInteger cnt;

        FakeResourceValidator(final String name) {
            this.label = name;
            this.cnt = new AtomicInteger(0);
        }

        @Override
        public Collection<Violation> validate(final Collection<File> files) {
            this.cnt.incrementAndGet();
            return Collections.emptyList();
        }

        @Override
        public String name() {
            return this.label;
        }

        public int count() {
            return this.cnt.get();
        }
    }

    /**
     * FakeMavenValidator.
     *
     * A mock to a MavenValidator.
     *
     * @since 0.24.1
     */
    private static final class FakeMavenValidator implements MavenValidator {
        /**
         * Method calls counter.
         */
        private final AtomicInteger cnt;

        FakeMavenValidator() {
            this.cnt = new AtomicInteger(0);
        }

        @Override
        public void validate(final MavenEnvironment env) {
            this.cnt.incrementAndGet();
        }

        public int count() {
            return this.cnt.get();
        }
    }
}
