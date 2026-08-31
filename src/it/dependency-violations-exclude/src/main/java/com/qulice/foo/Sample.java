/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.foo;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

/**
 * Test class.
 * @since 1.0
 */
public final class Sample {

    /**
     * Ctor.
     */
    public Sample() {
        // nothing to initialize
    }

    /**
     * Test method.
     * @return Stream
     * @checkstyle NonStaticMethod (2 lines)
     */
    public InputStream test() {
        return IOUtils.toInputStream("oops", StandardCharsets.UTF_8);
    }
}
