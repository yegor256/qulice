/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;

/**
 * Checks if two paths are equal.
 * @since 0.1
 */
final class PathPredicate implements Predicate<String> {

    /**
     * Path to match.
     */
    private final String name;

    /**
     * Constructor.
     * @param name Path to match
     */
    PathPredicate(final String name) {
        this.name = name;
    }

    @Override
    public boolean apply(@Nullable final String input) {
        return input != null
            && FilenameUtils.normalize(this.name, true).matches(input);
    }
}
