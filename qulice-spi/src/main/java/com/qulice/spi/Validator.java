/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

/**
 * Validator.
 *
 * @since 0.3
 */
public interface Validator {

    /**
     * Validate and throws exception if there are any problems.
     * @param env The environment to work with (must not be null)
     * @throws ValidationException In case of any violations found
     * @throws IllegalArgumentException If env is null
     */
    void validate(Environment env) throws ValidationException;

    /**
     * Name of this validator.
     * @return Name of this validator.
     */
    String name();
}
