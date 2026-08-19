/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.PrivilegedAction;
import java.util.List;

/**
 * Creates URL ClassLoader in privileged block.
 * @since 0.1
 */
final class PrivilegedClassLoader implements PrivilegedAction<URLClassLoader> {

    /**
     * URLs for class loading.
     */
    private final List<URL> urls;

    /**
     * Constructor.
     * @param urls URLs for class loading
     */
    PrivilegedClassLoader(final List<URL> urls) {
        this.urls = urls;
    }

    @Override
    public URLClassLoader run() {
        return new URLClassLoader(
            this.urls.toArray(new URL[0]),
            Thread.currentThread().getContextClassLoader()
        );
    }
}
