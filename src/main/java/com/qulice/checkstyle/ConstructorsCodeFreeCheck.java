/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that constructors do not contain any method calls.
 *
 * <p>A constructor must only assign fields from constructor parameters
 * or from newly created objects, and may delegate to another constructor
 * via {@code this(...)} or {@code super(...)}. Calling any method
 * (static or instance) from inside a constructor is forbidden,
 * including as the right-hand side of a field assignment
 * (e.g. {@code this.bar = Foo.createBar()}), as an argument to a
 * delegating constructor call, or as a nested argument to a {@code new}
 * expression.
 *
 * <p>Method calls nested inside lambda bodies or anonymous class bodies
 * are not considered constructor code, because they are not executed
 * at construction time: only the lambda object or the anonymous class
 * instance is created. Such subtrees are skipped.
 *
 * @since 0.24
 */
public final class ConstructorsCodeFreeCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.CTOR_DEF};
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final DetailAST body = ast.findFirstToken(TokenTypes.SLIST);
        if (body != null) {
            this.reportCalls(body);
        }
    }

    /**
     * Reports every method call found anywhere in the given subtree,
     * except those nested inside lambda bodies or anonymous class bodies.
     * @param node Root of the subtree to scan
     */
    private void reportCalls(final DetailAST node) {
        for (DetailAST child = node.getFirstChild();
            child != null; child = child.getNextSibling()) {
            final int type = child.getType();
            if (type == TokenTypes.LAMBDA || type == TokenTypes.OBJBLOCK) {
                continue;
            }
            if (type == TokenTypes.METHOD_CALL) {
                this.log(
                    child.getLineNo(),
                    "Constructor must not contain method calls"
                );
            }
            this.reportCalls(child);
        }
    }
}
