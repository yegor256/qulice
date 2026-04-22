/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.io.File;
import java.nio.file.Path;

/**
 * Path of a file relative to a base directory.
 *
 * <p>Given a base directory and a target file, computes the file path
 * relative to the base, prefixed with a forward slash and using forward
 * slashes as separators. Uses {@link Path#relativize(Path)} after
 * normalising both paths to absolute form, which correctly handles cases
 * where the file and base dir share a canonical location but differ in
 * string form (for example, on macOS the {@code /var} symlink to
 * {@code /private/var}). When the file lies outside the base directory,
 * returns the file's absolute path unchanged.</p>
 *
 * @since 0.24
 */
public final class Relative {

    /**
     * Base directory.
     */
    private final File base;

    /**
     * Target file.
     */
    private final File target;

    /**
     * Ctor.
     * @param base Base directory
     * @param target Target file
     */
    public Relative(final File base, final File target) {
        this.base = base;
        this.target = target;
    }

    /**
     * Path of the target file relative to the base directory.
     * @return Relative path starting with a forward slash, or the
     *  absolute path of the file if it is not under the base directory
     */
    public String path() {
        final Path root = this.base.toPath().toAbsolutePath().normalize();
        final Path file = this.target.toPath().toAbsolutePath().normalize();
        final String name;
        if (file.startsWith(root)) {
            name = "/".concat(
                root.relativize(file).toString().replace(File.separatorChar, '/')
            );
        } else {
            name = file.toString().replace(File.separatorChar, '/');
        }
        return name;
    }
}
