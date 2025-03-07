/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.jcabi.log.Logger;
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Check the project and find all possible violations.
 *
 * @since 0.3
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true)
public final class CheckMojo extends AbstractQuliceMojo {

    /**
     * Executors for validators.
     */
    private final ExecutorService executors =
        Executors.newFixedThreadPool(5);

    /**
     * Provider of validators.
     */
    private ValidatorsProvider provider =
        new DefaultValidatorsProvider(this.env());

    @Override
    public void doExecute() throws MojoFailureException {
        try {
            this.run();
        } catch (final ValidationException ex) {
            Logger.info(
                this,
                "Read our quality policy: http://www.qulice.com/quality.html"
            );
            throw new MojoFailureException("Failure", ex);
        }
    }

    /**
     * Set provider of validators.
     * @param prov The provider
     */
    public void setValidatorsProvider(final ValidatorsProvider prov) {
        this.provider = prov;
    }

    /**
     * Run them all.
     * @throws ValidationException If any of them fail
     */
    private void run() throws ValidationException {
        final LinkedList<Violation> results = new LinkedList<>();
        final MavenEnvironment env = this.env();
        final Collection<File> files = env.files("*.*");
        if (!files.isEmpty()) {
            final Collection<ResourceValidator> validators =
                this.provider.externalResource();
            final Collection<Future<Collection<Violation>>> futures =
                this.submit(env, files, validators);
            for (final Future<Collection<Violation>> future : futures) {
                try {
                    results.addAll(future.get(10L, TimeUnit.MINUTES));
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(ex);
                } catch (final ExecutionException | TimeoutException ex) {
                    throw new IllegalStateException(ex);
                }
            }
            Collections.sort(results);
            for (final Violation result : results) {
                Logger.info(
                    this,
                    "%s: %s[%s]: %s (%s)",
                    result.validator(),
                    result.file().replace(
                        String.format(
                            "%s/", this.session().getExecutionRootDirectory()
                        ),
                        ""
                    ),
                    result.lines(),
                    result.message(),
                    result.name()
                );
            }
        }
        if (!results.isEmpty()) {
            throw new ValidationException(
                String.format("There are %d violations", results.size())
           );
        }
        for (final Validator validator : this.provider.external()) {
            Logger.info(this, "Starting %s validator", validator.name());
            validator.validate(env);
            Logger.info(this, "Finishing %s validator", validator.name());
        }
        for (final MavenValidator validator : this.provider.internal()) {
            validator.validate(env);
        }
    }

    /**
     * Submit validators to executor.
     * @param env Maven environment
     * @param files List of files to validate
     * @param validators Validators to use
     * @return List of futures
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private Collection<Future<Collection<Violation>>> submit(
        final MavenEnvironment env, final Collection<File> files,
        final Collection<ResourceValidator> validators) {
        final Collection<Future<Collection<Violation>>> futures =
            new LinkedList<>();
        for (final ResourceValidator validator : validators) {
            futures.add(
                this.executors.submit(
                    new ValidatorCallable(validator, env, files)
                )
            );
        }
        return futures;
    }

    /**
     * Filter files based on excludes.
     * @param env Maven environment
     * @param files Files to exclude
     * @param validator Validator to use
     * @return Filtered files
     */
    private static Collection<File> filter(final MavenEnvironment env,
        final Collection<File> files, final ResourceValidator validator) {
        final Collection<File> filtered = new LinkedList<>();
        for (final File file : files) {
            if (
                !env.exclude(
                    validator.name().toLowerCase(Locale.ENGLISH),
                    file.toString()
                )
            ) {
                filtered.add(file);
            }
        }
        return filtered;
    }

    /**
     * Callable for validators.
     *
     * @since 0.1
     */
    private static class ValidatorCallable
        implements Callable<Collection<Violation>> {
        /**
         * Validator to use.
         */
        private final ResourceValidator validator;

        /**
         * Maven environment.
         */
        private final MavenEnvironment env;

        /**
         * List of files to validate.
         */
        private final Collection<File> files;

        /**
         * Constructor.
         * @param validator Validator to use
         * @param env Maven environment
         * @param files List of files to validate
         */
        ValidatorCallable(final ResourceValidator validator,
            final MavenEnvironment env, final Collection<File> files) {
            this.validator = validator;
            this.env = env;
            this.files = files;
        }

        @Override
        public Collection<Violation> call() {
            return this.validator.validate(
                CheckMojo.filter(this.env, this.files, this.validator)
            );
        }
    }
}
