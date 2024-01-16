/*
 * Copyright (c) 2011-2024 Qulice.com
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
