/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Validator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Mocker of ValidatorsProvider.
 * @since 0.4
 */
final class ValidatorsProviderMocker {

    /**
     * List of external validators.
     */
    private final Set<Validator> external = new HashSet<>();

    /**
     * List of external resource validators.
     */
    private final transient Set<ResourceValidator> rexternal = new HashSet<>();

    /**
     * List of internal validators.
     */
    private final Set<MavenValidator> internal = new HashSet<>();

    /**
     * With this external validator.
     * @param validator The validator
     * @return This object
     */
    public ValidatorsProviderMocker withExternal(final Validator validator) {
        this.external.add(validator);
        return this;
    }

    /**
     * With this external resource validator.
     * @param validator The validator
     * @return This object
     */
    public ValidatorsProviderMocker withExternalResource(
        final ResourceValidator validator) {
        this.rexternal.add(validator);
        return this;
    }

    /**
     * With this external validator.
     * @param validator The validator
     * @return This object
     */
    public ValidatorsProviderMocker withInternal(
        final MavenValidator validator) {
        this.internal.add(validator);
        return this;
    }

    /**
     * Mock it.
     * @return The provider
     */
    public ValidatorsProvider mock() {
        return new FakeValidatorsProvider(
            this.internal,
            this.external,
            this.rexternal
        );
    }

    /**
     * FakeValidatorsProvides.
     * A mock to ValidatorsProvides.
     *
     * @since 0.24.1
     */
    private static class FakeValidatorsProvider implements ValidatorsProvider {
        /**
         * Max validators.
         */
        private final Set<MavenValidator> intern;

        /**
         * External validators.
         */
        private final Set<Validator> extern;

        /**
         * Resources validators.
         */
        private final Set<ResourceValidator> rextern;

        FakeValidatorsProvider(
            final Set<MavenValidator> inter,
            final Set<Validator> exter,
            final Set<ResourceValidator> rexter
        ) {
            this.intern = inter;
            this.extern = exter;
            this.rextern = rexter;
        }

        @Override
        public Set<MavenValidator> internal() {
            return this.intern;
        }

        @Override
        public Set<Validator> external() {
            return this.extern;
        }

        @Override
        public Collection<ResourceValidator> externalResource() {
            return this.rextern;
        }
    }
}
