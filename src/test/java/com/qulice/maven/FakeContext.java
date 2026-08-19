/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.util.Collections;
import java.util.Map;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;

/**
 * FakeContext.
 * A mock to a context.
 * @since 0.24.1
 */
final class FakeContext implements Context {

    /**
     * Container.
     */
    private final PlexusContainer container;

    FakeContext(final PlexusContainer ctainer) {
        this.container = ctainer;
    }

    @Override
    public boolean contains(final Object obj) {
        return true;
    }

    @Override
    public void put(final Object obja, final Object objb) {
        // Intentionally left blank
    }

    @Override
    public Object get(final Object obj) throws ContextException {
        return this.container;
    }

    @Override
    public Map<Object, Object> getContextData() {
        return Collections.emptyMap();
    }
}
