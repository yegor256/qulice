/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Validator;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.Context;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        final Log log = Mockito.mock(Log.class);
        mojo.setLog(log);
        mojo.setSkip(true);
        mojo.execute();
        Mockito.verify(log).info("Execution skipped");
    }

    /**
     * CheckMojo can validate a project using all provided validators.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void validatesUsingAllProvidedValidators() throws Exception {
        final CheckMojo mojo = new CheckMojo();
        final Validator external = Mockito.mock(Validator.class);
        Mockito.when(external.name()).thenReturn("somename");
        final ResourceValidator rexternal =
            Mockito.mock(ResourceValidator.class);
        Mockito.when(rexternal.name()).thenReturn("other");
        final MavenValidator internal = Mockito.mock(MavenValidator.class);
        final ValidatorsProvider provider = new ValidatorsProviderMocker()
            .withInternal(internal)
            .withExternal(external)
            .withExternalResource(rexternal)
            .mock();
        mojo.setValidatorsProvider(provider);
        final MavenProject project = Mockito.mock(MavenProject.class);
        mojo.setProject(project);
        mojo.setLog(Mockito.mock(Log.class));
        final Context context = Mockito.mock(Context.class);
        mojo.contextualize(context);
        mojo.execute();
        Mockito.verify(internal).validate(Mockito.any(MavenEnvironment.class));
        Mockito.verify(external).validate(Mockito.any(Environment.class));
        Mockito.verify(rexternal, Mockito.atLeastOnce())
            .validate(Mockito.anyCollection());
    }

}
