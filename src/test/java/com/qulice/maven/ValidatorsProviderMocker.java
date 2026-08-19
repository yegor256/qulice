/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Validator;
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
    ValidatorsProviderMocker withExternal(final Validator validator) {
        this.external.add(validator);
        return this;
    }

    /**
     * With this external resource validator.
     * @param validator The validator
     * @return This object
     */
    ValidatorsProviderMocker withExternalResource(final ResourceValidator validator) {
        this.rexternal.add(validator);
        return this;
    }

    /**
     * With this external validator.
     * @param validator The validator
     * @return This object
     */
    ValidatorsProviderMocker withInternal(final MavenValidator validator) {
        this.internal.add(validator);
        return this;
    }

    /**
     * Mock it.
     * @return The provider
     */
    ValidatorsProvider mock() {
        return new FakeValidatorsProvider(
            this.internal,
            this.external,
            this.rexternal
        );
    }
}
