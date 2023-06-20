/*
 * Copyright (c) 2011-2022 Qulice.com
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
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Temporary utility class.
 *
 * Some checks used equalsTree() method in DetailAST
 * which recursively compared two subtrees.
 * However, this method was deprecated in upstream, so here follows it's
 * simple re-implementation.
 *
 * @since 1.0
 *
 * @todo #1148 Avoid equalsTree usages in checks and delete this class
 */
class EqualsTree {
    /**
     * Wrapped node.
     */
    private final DetailAST node;

    /**
     * Creates an EqualsTree decorator wrapping the given node.
     * @param node Node which will be represented by the new EqualsTree instance
     */
    EqualsTree(final DetailAST node) {
        this.node = node;
    }

    /**
     * Checks that `this` node subtree is recursively equal
     * to `other` node subtree.
     * @param other Node to compare with
     * @return Comparison result
     */
    boolean equalsTree(final DetailAST other) {
        final Stream<DetailAST> children = new ChildStream(this.node).children();
        final Iterator<DetailAST> nephews = new ChildStream(other).children().iterator();
        return this.equalsShallow(other)
            && children
                .allMatch(
                    child -> nephews.hasNext()
                        && new EqualsTree(child).equalsTree(nephews.next())
                )
                && !nephews.hasNext();
    }

    /**
     * Checks that `this` node is equal to `other` node.
     * This method does not take any children into consideration.
     * @param other Node to compare with
     * @return Comparison result
     */
    private boolean equalsShallow(final DetailAST other) {
        return this.node.getType() == other.getType()
            && this.node.getText().equals(other.getText());
    }
}
