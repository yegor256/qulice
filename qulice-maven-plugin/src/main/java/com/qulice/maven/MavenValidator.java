/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.ValidationException;

/**
 * Validator inside Maven.
 *
 * @since 0.3
 */
interface MavenValidator {

    /**
     * Validate this environment.
     * @param env The environment
     * @throws ValidationException In case of violations
     */
    void validate(MavenEnvironment env) throws ValidationException;

}
