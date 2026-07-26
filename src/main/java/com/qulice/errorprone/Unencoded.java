/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.errorprone;

import com.jcabi.log.Logger;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

/**
 * A classpath entry behind a {@code file:} URL, with its percent-encoding
 * undone.
 *
 * <p>{@link URL#getPath()} hands back the encoded path component, so a jar
 * living under a directory whose name contains a space or any other
 * URL-reserved character keeps its {@code %20} and the forked {@code javac}
 * finds no file by that name. Going through {@link java.net.URI} turns the
 * escapes back into the characters that are really on disk. URLs of any
 * other protocol point at nothing local and resolve to nothing.</p>
 *
 * @since 1.0
 */
final class Unencoded {

    /**
     * URL of a classpath entry.
     */
    private final URL url;

    /**
     * Constructor.
     * @param url URL of a classpath entry
     */
    Unencoded(final URL url) {
        this.url = url;
    }

    /**
     * Absolute path of the file this URL points at.
     * @return The path, or empty if the URL is not a local file
     */
    Optional<String> path() {
        Optional<String> path = Optional.empty();
        if ("file".equals(this.url.getProtocol())) {
            try {
                path = Optional.of(
                    new File(this.url.toURI()).getAbsolutePath()
                );
            } catch (final URISyntaxException | IllegalArgumentException ex) {
                Logger.debug(
                    this, "Cannot resolve %s: %s", this.url, ex.getMessage()
                );
            }
        }
        return path;
    }
}
