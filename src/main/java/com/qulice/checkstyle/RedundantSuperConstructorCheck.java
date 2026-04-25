/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that constructors do not contain a redundant {@code super()}
 * call when the enclosing class does not extend any class explicitly.
 *
 * <p>A class without an {@code extends} clause implicitly extends
 * {@link Object}. Calling {@code super()} in its constructor invokes
 * the {@link Object} constructor, which the compiler inserts on its own.
 * The explicit call is therefore redundant and is most often a sign
 * of confusion between class extension and interface implementation,
 * for example:
 *
 * <pre>
 * public class NewAgent implements Agent {
 *     public NewAgent() {
 *         super();
 *     }
 * }
 * </pre>
 *
 * <p>The rule only applies to constructors of {@code class} declarations.
 * It does not apply to records or enums, where {@code super(...)}
 * either targets a fixed superclass or is illegal.
 *
 * @since 0.24
 */
public final class RedundantSuperConstructorCheck extends AbstractCheck {

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
        final DetailAST clazz = RedundantSuperConstructorCheck.enclosingClass(ast);
        if (clazz != null
            && clazz.findFirstToken(TokenTypes.EXTENDS_CLAUSE) == null) {
            final DetailAST body = ast.findFirstToken(TokenTypes.SLIST);
            if (body != null) {
                this.reportSuperCalls(body);
            }
        }
    }

    /**
     * Find the enclosing {@code CLASS_DEF} of the given constructor node.
     * @param ctor Constructor node
     * @return The CLASS_DEF, or null if the enclosing type is not a class
     */
    private static DetailAST enclosingClass(final DetailAST ctor) {
        DetailAST result = null;
        final DetailAST block = ctor.getParent();
        if (block != null && block.getType() == TokenTypes.OBJBLOCK) {
            final DetailAST owner = block.getParent();
            if (owner != null && owner.getType() == TokenTypes.CLASS_DEF) {
                result = owner;
            }
        }
        return result;
    }

    /**
     * Report any direct {@code super(...)} call inside the constructor body.
     * Nested anonymous class bodies and lambdas are skipped because their
     * {@code super(...)} calls belong to a different enclosing class.
     * @param node Root of the constructor body subtree
     */
    private void reportSuperCalls(final DetailAST node) {
        for (DetailAST child = node.getFirstChild();
            child != null; child = child.getNextSibling()) {
            final int type = child.getType();
            if (type == TokenTypes.LAMBDA
                || type == TokenTypes.OBJBLOCK
                || type == TokenTypes.CLASS_DEF) {
                continue;
            }
            if (type == TokenTypes.SUPER_CTOR_CALL) {
                this.log(
                    child.getLineNo(),
                    "Redundant super() call when class does not extend any other class"
                );
            }
            this.reportSuperCalls(child);
        }
    }
}
