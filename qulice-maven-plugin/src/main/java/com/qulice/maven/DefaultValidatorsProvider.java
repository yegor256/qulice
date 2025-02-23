/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.checkstyle.CheckstyleValidator;
import com.qulice.pmd.PmdValidator;
import com.qulice.spi.Environment;
import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Validator;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provider of validators.
 *
 * @since 0.3
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
final class DefaultValidatorsProvider implements ValidatorsProvider {
    /**
     * Environment to use for validation.
     */
    private final Environment env;

    /**
     * Constructor.
     * @param env Environment to use for validation.
     */
    DefaultValidatorsProvider(final Environment env) {
        this.env = env;
    }

    @Override
    public Set<MavenValidator> internal() {
        final Set<MavenValidator> validators = new LinkedHashSet<>();
        validators.add(new PomXpathValidator());
        validators.add(new EnforcerValidator());
        validators.add(new DuplicateFinderValidator());
        validators.add(new SvnPropertiesValidator());
        validators.add(new DependenciesValidator());
        validators.add(new SnapshotsValidator());
        return validators;
    }

    @Override
    public Set<Validator> external() {
        return new LinkedHashSet<>();
    }

    @Override
    public Collection<ResourceValidator> externalResource() {
        return Arrays.asList(
            new CheckstyleValidator(this.env),
            new PmdValidator(this.env)
        );
    }
}
