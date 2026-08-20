/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

import java.util.List;

/**
 * A file Qulice leaves alone, wherever the project keeps it.
 *
 * <p>Three directories of a Maven project hold Java files that are not
 * sources of the product, and Checkstyle, PMD and ErrorProne have
 * nothing useful to say about any of them:</p>
 *
 * <ul>
 * <li>{@code src/test/resources} holds fixtures: inputs to the tests,
 * which Maven never compiles and which quite often are broken on
 * purpose, because that is exactly what the test needs them to be;</li>
 * <li>{@code src/site} holds the sources of the Maven site, where a
 * {@code .java} file is an illustration in the documentation rather
 * than code that ships;</li>
 * <li>{@code src/it} holds whole projects of their own, the ones
 * {@code maven-invoker-plugin} builds, each with its own POM and its
 * own idea of what good code looks like.</li>
 * </ul>
 *
 * <p>All three are skipped by default, without the project having to
 * say so through an {@code <exclude>} of its own.</p>
 *
 * <p>The path handed to the constructor is the one {@link Relative}
 * makes: forward slashes, relative to the base directory of the project
 * and starting with a slash, or absolute when the file lies outside of
 * it. Both forms are recognised, and so is a file of a nested Maven
 * module, whose own {@code src/site} the path merely contains. The
 * trailing slash of each directory is part of the match, so a
 * {@code src/itest} of someone else's making stays in.</p>
 *
 * @since 1.0
 */
public final class Ignored {

    /**
     * The directories left alone, by Maven convention.
     */
    private static final List<String> DIRS = List.of(
        "/src/test/resources/",
        "/src/site/",
        "/src/it/"
    );

    /**
     * The path of the file.
     */
    private final String path;

    /**
     * Ctor.
     * @param file Path of the file, the way {@link Relative} makes it
     */
    public Ignored(final String file) {
        this.path = file;
    }

    /**
     * Is this file ignored?
     * @return TRUE if the file is inside one of the ignored directories
     */
    public boolean yes() {
        return Ignored.DIRS.stream().anyMatch(this.path::contains);
    }
}
