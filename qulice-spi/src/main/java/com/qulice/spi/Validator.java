/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
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
     * @param env The environment to work with
     * @throws ValidationException In case of any violations found
     */
    void validate(Environment env) throws ValidationException;

    /**
     * Name of this validator.
     * @return Name of this validator.
     */
    String name();
}
