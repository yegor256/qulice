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
 * Provider of validators.
 *
 * @since 0.3
 */
interface ValidatorsProvider {

    /**
     * Get a collection of internal validators.
     * @return List of them
     * @see CheckMojo#execute()
     */
    Set<MavenValidator> internal();

    /**
     * Get a collection of external validators.
     * @return List of them
     * @see CheckMojo#execute()
     */
    Set<Validator> external();

    /**
     * Get a collection of external validators.
     * @return List of them
     * @see CheckMojo#execute()
     */
    Collection<ResourceValidator> externalResource();
}
