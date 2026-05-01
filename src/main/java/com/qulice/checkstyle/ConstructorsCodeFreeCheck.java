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
 * <p>Defensive array copy idioms — {@code Arrays.copyOf(...)} and
 * {@code <expr>.clone()} — are also tolerated, since there is no
 * method-call-free way to defensively copy an array field at
 * construction time (Effective Java item 50).
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
     * except those nested inside lambda bodies or anonymous class bodies,
     * and except sanctioned defensive-copy idioms.
     * @param node Root of the subtree to scan
     */
    private void reportCalls(final DetailAST node) {
        for (DetailAST child = node.getFirstChild();
            child != null; child = child.getNextSibling()) {
            final int type = child.getType();
            if (type == TokenTypes.LAMBDA || type == TokenTypes.OBJBLOCK) {
                continue;
            }
            if (type == TokenTypes.METHOD_CALL
                && !ConstructorsCodeFreeCheck.isDefensiveCopy(child)) {
                this.log(
                    child.getLineNo(),
                    "Constructor must not contain method calls"
                );
            }
            this.reportCalls(child);
        }
    }

    /**
     * Is this method call a defensive array-copy idiom?
     *
     * <p>Recognizes {@code Arrays.copyOf(...)} (static, any qualifier
     * ending in {@code Arrays.copyOf}) and any zero-argument
     * {@code <expr>.clone()} call.
     *
     * @param call A {@code METHOD_CALL} AST node
     * @return True if the call is a sanctioned defensive copy
     */
    private static boolean isDefensiveCopy(final DetailAST call) {
        final DetailAST dot = call.getFirstChild();
        final boolean defensive;
        if (dot == null || dot.getType() != TokenTypes.DOT) {
            defensive = false;
        } else {
            final DetailAST method = dot.getLastChild();
            defensive = method != null
                && method.getType() == TokenTypes.IDENT
                && (
                    ConstructorsCodeFreeCheck.isArraysCopyOf(dot, method)
                        || ConstructorsCodeFreeCheck.isArrayClone(call, method)
                );
        }
        return defensive;
    }

    /**
     * Is this an {@code Arrays.copyOf(...)} call?
     * @param dot The {@code DOT} node that is first child of {@code METHOD_CALL}
     * @param method The {@code IDENT} that is the called method's name
     * @return True if it matches the {@code Arrays.copyOf} idiom
     */
    private static boolean isArraysCopyOf(
        final DetailAST dot, final DetailAST method
    ) {
        final DetailAST qualifier = dot.getFirstChild();
        return "copyOf".equals(method.getText())
            && qualifier != null
            && ConstructorsCodeFreeCheck.endsWith(qualifier, "Arrays");
    }

    /**
     * Is this a no-argument {@code <expr>.clone()} call?
     * @param call The {@code METHOD_CALL} AST node
     * @param method The {@code IDENT} that is the called method's name
     * @return True if it matches the array-clone idiom
     */
    private static boolean isArrayClone(
        final DetailAST call, final DetailAST method
    ) {
        final DetailAST elist = call.findFirstToken(TokenTypes.ELIST);
        return "clone".equals(method.getText())
            && elist != null
            && elist.getFirstChild() == null;
    }

    /**
     * Does the qualifier expression end in the given identifier?
     *
     * <p>Handles a bare {@code IDENT} (e.g. {@code Arrays}) and a
     * dotted chain (e.g. {@code java.util.Arrays}), where the final
     * segment of the chain is the identifier we look for.
     *
     * @param node The qualifier AST node
     * @param name The expected last identifier
     * @return True if the qualifier's last segment matches
     */
    private static boolean endsWith(final DetailAST node, final String name) {
        final boolean match;
        if (node.getType() == TokenTypes.IDENT) {
            match = name.equals(node.getText());
        } else if (node.getType() == TokenTypes.DOT) {
            final DetailAST last = node.getLastChild();
            match = last != null
                && last.getType() == TokenTypes.IDENT
                && name.equals(last.getText());
        } else {
            match = false;
        }
        return match;
    }
}
