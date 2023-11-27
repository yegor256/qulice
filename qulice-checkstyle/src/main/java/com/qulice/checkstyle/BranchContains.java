/*
 * Copyright (c) 2011-2023 Qulice.com
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

/**
 * Utility class that checks the existence
 * of tokens of specific type in the AST node subtree.
 *
 * Some checks used branchContains() method in DetailAST
 * which recursively searched node subtree for the child of a given type.
 * However, this method was deprecated in upstream due to unintended too
 * deep scanning. It is recommended to write traversal implementation
 * for your needs by yourself to avoid unexpected side effects. So here follows it's
 * simple implementation.
 *
 * @since 1.0
 */
class BranchContains {
    /**
     * Node, represented by this object.
     */
    private final DetailAST node;

    /**
     * Creates a decorator which is able to search in the node's subtree.
     * @param node Node which will be represented by the new BranchContains instance.
     */
    BranchContains(final DetailAST node) {
        this.node = node;
    }

    /**
     * Checks if there is a node of type `type` in this node subtree.
     * The root node itself may also match, i.e.
     * `new BranchContains(node).contains(node.getType())` is always true
     * @param type Desired type
     * @return Whether node of given type exists somewhere in the subtree
     */
    boolean check(final int type) {
        return this.node.getType() == type
            || new ChildStream(this.node)
            .children()
            .anyMatch(child -> new BranchContains(child).check(type));
    }
}
