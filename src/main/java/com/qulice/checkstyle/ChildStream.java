/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import java.util.stream.Stream;

/**
 * Utility class which simplifies traversing DetailAST objects.
 *
 * DetailAST APIs for working with child nodes require writing
 * imperative code, which generally looks less readable then
 * declarative Stream manipulations. This class integrates DetailAST
 * with Java Streams.
 *
 * @since 1.0
 */
class ChildStream {
    /**
     * Node, whose children will be traversed by this ChildStream object.
     */
    private final DetailAST node;

    /**
     * Creates a new child stream factory.
     *
     * @param node Node which will used by this object.
     */
    ChildStream(final DetailAST node) {
        this.node = node;
    }

    /**
     * Creates a new stream which sequentially yields all
     * children. Any two streams returned by this method are
     * independent.
     *
     * Implementation may be simplified using Stream.iterate when Java 8 support
     * is dropped.
     *
     * @return Stream of children
     */
    Stream<DetailAST> children() {
        final Stream.Builder<DetailAST> builder = Stream.builder();
        DetailAST child = this.node.getFirstChild();
        while (child != null) {
            builder.accept(child);
            child = child.getNextSibling();
        }
        return builder.build();
    }
}
