/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.jcabi.log.Logger;
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Check the project and find all possible violations.
 * @since 0.3
 */
@Mojo(
    name = "check",
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true
)
public final class CheckMojo extends AbstractQuliceMojo {

    /**
     * Executors for validators.
     */
    private final ExecutorService executors;

    /**
     * Provider of validators, if it was set from the outside.
     */
    private ValidatorsProvider provider;

    /**
     * Check timeout.
     * Can be a number of minutes.
     * Can be a string with time units, like '10m' or '1h'.
     * Time units are 's' for seconds, 'm' for minutes, 'h' for hours.
     * Can also be a string 'forever' to disable timeout.
     * Defaults to 10 minutes.
     */
    @Parameter(property = "qulice.check-timeout", defaultValue = "10")
    private String timeout;

    /**
     * Default constructor.
     */
    public CheckMojo() {
        this(Executors.newFixedThreadPool(5));
    }

    /**
     * Primary constructor.
     * @param svc Executors to run resource validators in
     */
    private CheckMojo(final ExecutorService svc) {
        this.executors = svc;
    }

    @Override
    public String doExecute() throws MojoFailureException {
        try {
            return this.run();
        } catch (final ValidationException ex) {
            Logger.info(
                this,
                "Read our quality policy: https://www.qulice.com/quality.html"
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
     * Set timeout for checks.
     * @param time Timeout value
     */
    public void setTimeout(final String time) {
        this.timeout = time;
    }

    /**
     * Filter files based on excludes.
     * @param env Maven environment
     * @param files Files to exclude
     * @param validator Validator to use
     * @return Filtered files
     */
    static Collection<File> filter(
        final MavenEnvironment env,
        final Collection<File> files, final ResourceValidator validator
    ) {
        final Collection<File> filtered = new ArrayList<>(files.size());
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
     * Run them all.
     * @return What was checked, for the final log line
     * @throws ValidationException If any of them fail
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private String run() throws ValidationException {
        final List<Violation> results = new ArrayList<>(0);
        final MavenEnvironment env = this.env();
        final ValidatorsProvider prov = this.validators(env);
        final Collection<ResourceValidator> resources = prov.externalResource();
        final Collection<File> files = env.files("*.*");
        if (!files.isEmpty()) {
            final Collection<Future<Collection<Violation>>> futures =
                this.submit(env, files, resources);
            for (final Future<Collection<Violation>> future : futures) {
                try {
                    if ("forever".equalsIgnoreCase(this.timeout)) {
                        results.addAll(future.get());
                    } else {
                        final long value = this.timeoutValue();
                        final TimeUnit units = this.timeoutUnits();
                        Logger.debug(
                            this,
                            "Waiting up to %d %s for validator result",
                            value,
                            units
                        );
                        results.addAll(future.get(value, units));
                    }
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
        for (final Validator validator : prov.external()) {
            Logger.info(this, "Starting %s validator", validator.name());
            validator.validate(env);
            Logger.info(this, "Finishing %s validator", validator.name());
        }
        for (final MavenValidator validator : prov.internal()) {
            validator.validate(env);
        }
        return new Summary(files, resources).toString();
    }

    /**
     * Provider of validators, the one that was set or the default one.
     * @param env Maven environment for the default provider
     * @return The provider
     */
    private ValidatorsProvider validators(final MavenEnvironment env) {
        final ValidatorsProvider prov;
        if (this.provider == null) {
            prov = new DefaultValidatorsProvider(env);
        } else {
            prov = this.provider;
        }
        return prov;
    }

    /**
     * Submit validators to executor.
     * @param env Maven environment
     * @param files List of files to validate
     * @param validators Validators to use
     * @return List of futures
     */
    private Collection<Future<Collection<Violation>>> submit(
        final MavenEnvironment env, final Collection<File> files,
        final Collection<ResourceValidator> validators
    ) {
        final Collection<Future<Collection<Violation>>> futures =
            new ArrayList<>(validators.size());
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
     * Timeout value for timeout.
     * @return Timeout value
     */
    private long timeoutValue() {
        final String clear = this.clearTimeout();
        final long res;
        if (clear.isEmpty()) {
            res = 10L;
        } else if (clear.endsWith("s") || clear.endsWith("m") || clear.endsWith("h")) {
            res = Long.parseLong(clear.substring(0, clear.length() - 1));
        } else {
            res = Long.parseLong(clear);
        }
        return res;
    }

    /**
     * Time unit for timeout.
     * @return Time unit
     */
    private TimeUnit timeoutUnits() {
        final String clear = this.clearTimeout();
        final TimeUnit unit;
        if (clear.endsWith("s")) {
            unit = TimeUnit.SECONDS;
        } else if (clear.endsWith("m")) {
            unit = TimeUnit.MINUTES;
        } else if (clear.endsWith("h")) {
            unit = TimeUnit.HOURS;
        } else {
            unit = TimeUnit.MINUTES;
        }
        return unit;
    }

    /**
     * Clear timeout string.
     * @return Cleaned timeout
     */
    private String clearTimeout() {
        final String clear;
        if (this.timeout == null) {
            clear = "";
        } else {
            clear = this.timeout.trim()
            .replaceAll(" ", "")
            .toLowerCase(Locale.ENGLISH);
        }
        return clear;
    }
}
