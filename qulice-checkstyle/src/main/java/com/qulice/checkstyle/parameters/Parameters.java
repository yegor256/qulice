/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.parameters;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract parameters. Is used for Generic type parameters or
 *  method(constructor) arguments.
 *
 * @since 0.18.18
 */
public class Parameters {

    /**
     * Class, interface, constructor or method definition node.
     */
    private final DetailAST node;

    /**
     * Parent TokenType (TYPE_PARAMETERS or PARAMETERS).
     * @see com.puppycrawl.tools.checkstyle.api.TokenTypes
     */
    private final int parent;

    /**
     * Children TokenType (TYPE_PARAMETER or PARAMETER_DEF).
     * @see com.puppycrawl.tools.checkstyle.api.TokenTypes
     */
    private final int children;

    /**
     * Primary ctor.
     * @param node Class, interface, constructor or method definition node.
     * @param parent Parent TokenType (TYPE_PARAMETERS or PARAMETERS).
     * @param children Children TokenType (TYPE_PARAMETER or PARAMETER_DEF).
     */
    public Parameters(
        final DetailAST node, final int parent, final int children
    ) {
        this.node = node;
        this.parent = parent;
        this.children = children;
    }

    /**
     * Return number of arguments.
     * @return Number of parameters.
     */
    public final int count() {
        final int result;
        final DetailAST params = this.node.findFirstToken(this.parent);
        if (params == null) {
            result = 0;
        } else {
            result = params.getChildCount(this.children);
        }
        return result;
    }

    /**
     * Return parameters for this node.
     * @return Parameters for this node.
     */
    public final List<DetailAST> parameters() {
        final List<DetailAST> result;
        final int count = this.count();
        if (count == 0) {
            result = Collections.emptyList();
        } else {
            final DetailAST params = this.node.findFirstToken(this.parent);
            result = new ArrayList<>(count);
            DetailAST child = params.getFirstChild();
            while (child != null) {
                if (child.getType() == this.children) {
                    result.add(child);
                }
                child = child.getNextSibling();
            }
        }
        return result;
    }
}
