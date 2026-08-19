/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.ResourceValidator;
import com.qulice.spi.Validator;
import java.util.Collection;
import java.util.Set;

/**
 * FakeValidatorsProvides.
 * A mock to ValidatorsProvides.
 * @since 0.24.1
 */
final class FakeValidatorsProvider implements ValidatorsProvider {

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
