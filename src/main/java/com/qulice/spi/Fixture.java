/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.spi;

/**
 * A file that is a fixture of the tests, rather than a source of the
 * product.
 *
 * <p>Whatever lies under {@code src/test/resources} is a fixture: an
 * input to the tests rather than a part of the product. Maven compiles
 * none of the {@code .java} files there, and quite often they are
 * deliberately broken, because that is exactly what the test needs them
 * to be. Feeding them to Checkstyle, PMD or ErrorProne produces nothing
 * but noise, so all three validators leave them alone by default,
 * without the project having to say so through an {@code <exclude>} of
 * its own.</p>
 *
 * <p>The path handed to the constructor is the one {@link Relative}
 * makes: forward slashes, relative to the base directory of the project
 * and starting with a slash, or absolute when the file lies outside of
 * it. Both forms are recognised, and so is a file of a nested Maven
 * module, whose own {@code src/test/resources} the path merely
 * contains.</p>
 *
 * @since 1.0
 */
public final class Fixture {

    /**
     * The directory test resources live in, by Maven convention.
     */
    private static final String DIR = "/src/test/resources/";

    /**
     * The path of the file.
     */
    private final String path;

    /**
     * Ctor.
     * @param file Path of the file, the way {@link Relative} makes it
     */
    public Fixture(final String file) {
        this.path = file;
    }

    /**
     * Is this file a fixture?
     * @return TRUE if the file is inside {@code src/test/resources}
     */
    public boolean yes() {
        return this.path.contains(Fixture.DIR);
    }
}
