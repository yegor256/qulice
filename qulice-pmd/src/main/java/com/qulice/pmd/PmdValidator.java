/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.qulice.spi.Environment;
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Validates source code with PMD.
 *
 * @since 0.3
 */
public final class PmdValidator implements ResourceValidator {

    /**
     * Environment to use.
     */
    private final transient Environment env;

    /**
     * Constructor.
     * @param env Environment to use.
     */
    public PmdValidator(final Environment env) {
        this.env = env;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Collection<Violation> validate(final Collection<File> files) {
        final SourceValidator validator = new SourceValidator(this.env);
        final Collection<File> sources = this.getNonExcludedFiles(files);
        final Collection<PmdError> errors = validator.validate(
            sources, this.env.basedir().getPath()
        );
        final Collection<Violation> violations = new LinkedList<>();
        for (final PmdError error : errors) {
            violations.add(
                new Violation.Default(
                    this.name(),
                    error.name(),
                    error.fileName(),
                    error.lines(),
                    error.description()
                )
            );
        }
        return violations;
    }

    @Override
    public String name() {
        return "PMD";
    }

    /**
     * Filters out excluded files from further validation.
     * @param files Files to validate
     * @return Relevant source files
     */
    public Collection<File> getNonExcludedFiles(final Collection<File> files) {
        final Collection<File> sources = new LinkedList<>();
        for (final File file : files) {
            final String name = file.getPath().substring(
                this.env.basedir().toString().length()
            );
            if (this.env.exclude("pmd", name)) {
                continue;
            }
            if (!name.matches("^.*\\.java$")) {
                continue;
            }
            sources.add(file);
        }
        return sources;
    }
}
