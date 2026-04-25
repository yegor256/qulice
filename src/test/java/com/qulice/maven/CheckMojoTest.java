/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.util.concurrent.TimeoutException;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.DefaultContext;
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
        mojo.setLog(new DefaultLog(logger));
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
        final BlockedValidator validator = new BlockedValidator();
        mojo.setValidatorsProvider(
            new ValidatorsProviderMocker()
                .withExternalResource(validator)
                .mock()
        );
        mojo.setProject(new MavenProject());
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
        mojo.setValidatorsProvider(
            new ValidatorsProviderMocker()
                .withExternalResource(new BlockedValidator())
                .mock()
        );
        mojo.setProject(new MavenProject());
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
        mojo.setValidatorsProvider(
            new ValidatorsProviderMocker()
                .withInternal(internal)
                .withExternal(external)
                .withExternalResource(rexternal)
                .mock()
        );
        mojo.setProject(new MavenProject());
        mojo.setLog(new DefaultLog(new FakeLogger()));
        mojo.contextualize(new DefaultContext());
        mojo.execute();
        Assertions.assertAll(
            () -> Assertions.assertEquals(1, internal.count()),
            () -> Assertions.assertEquals(1, external.count()),
            () -> Assertions.assertEquals(1, rexternal.count())
        );
    }
}
