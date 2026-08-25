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
 * (e.g. {@code this.bar = Foo.createBar()}) or as a nested argument
 * to a {@code new} expression.</p>
 *
 * <p>A constructor whose only statement is a delegating
 * {@code this(...)} or {@code super(...)} call is exempt: such a
 * constructor performs no work of its own, and the method-call
 * arguments to the delegate (e.g. {@code this(System.currentTimeMillis())})
 * are accepted as a sanctioned factory-style entry point.</p>
 *
 * <p>Method calls nested inside lambda bodies or anonymous class bodies
 * are not considered constructor code, because they are not executed
 * at construction time: only the lambda object or the anonymous class
 * instance is created. Such subtrees are skipped.</p>
 *
 * <p>Defensive array copy idioms — {@code Arrays.copyOf(...)} and
 * {@code <expr>.clone()} — are also tolerated, since there is no
 * method-call-free way to defensively copy an array field at
 * construction time (Effective Java item 50).</p>
 *
 * @since 0.24
 */
public final class ConstructorsCodeFreeCheck extends AbstractCheck {

    /**
     * Default constructor.
     */
    public ConstructorsCodeFreeCheck() {
        // nothing to initialize
    }

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
        if (body != null && !ConstructorsCodeFreeCheck.isOnlyDelegate(body)) {
            this.reportCalls(body);
        }
    }

    private static boolean isOnlyDelegate(final DetailAST body) {
        final DetailAST first = body.getFirstChild();
        final boolean delegate;
        if (ConstructorsCodeFreeCheck.isDelegate(first)) {
            final DetailAST next = first.getNextSibling();
            delegate = next == null || next.getType() == TokenTypes.RCURLY;
        } else {
            delegate = false;
        }
        return delegate;
    }

    private static boolean isDelegate(final DetailAST node) {
        final boolean delegate;
        if (node == null) {
            delegate = false;
        } else {
            delegate = node.getType() == TokenTypes.CTOR_CALL
                || node.getType() == TokenTypes.SUPER_CTOR_CALL;
        }
        return delegate;
    }

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

    private static boolean isArraysCopyOf(
        final DetailAST dot, final DetailAST method
    ) {
        final DetailAST qualifier = dot.getFirstChild();
        return "copyOf".equals(method.getText())
            && qualifier != null
            && ConstructorsCodeFreeCheck.endsWith(qualifier, "Arrays");
    }

    private static boolean isArrayClone(
        final DetailAST call, final DetailAST method
    ) {
        final DetailAST elist = call.findFirstToken(TokenTypes.ELIST);
        return "clone".equals(method.getText())
            && elist != null
            && elist.getFirstChild() == null;
    }

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
