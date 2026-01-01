/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.io.File;
import java.util.Collection;

/**
 * Validator.
 *
 * @since 0.17
 */
public interface ResourceValidator {

    /**
     * Validate and throws exception if there are any problems.
     * @param files Files to validate
     * @return Validation results
     */
    Collection<Violation> validate(Collection<File> files);

    /**
     * Name of this validator.
     * @return Name of this validator.
     */
    String name();
}
