/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Callable for validators.
 * @since 0.1
 */
final class ValidatorCallable implements Callable<Collection<Violation>> {

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
    ValidatorCallable(
        final ResourceValidator validator,
        final MavenEnvironment env, final Collection<File> files
    ) {
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
