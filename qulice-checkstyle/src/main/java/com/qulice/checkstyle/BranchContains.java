/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
