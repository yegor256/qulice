/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.foo;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 * Test class.
 * @since 1.0
 */
public final class Sample {

    /**
     * Test method.
     * @return Stream.
     * @checkstyle NonStaticMethod (2 lines)
     */
    public InputStream test() throws IOException {
        return IOUtils.toInputStream("oops", "UTF-8");
    }
}
