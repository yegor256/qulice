/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks if inner classes are properly accessed using their qualified name
 * with the outer class.
 *
 * @since 0.18
 * @todo #738:30min Static inner classes should be qualified with outer class
 *  Implement QualifyInnerClassCheck so it follows what defined in
 *  QualifyInnerClassCheck test and add this check to checks.xml and CheckTest.
 */
public final class QualifyInnerClassCheck extends AbstractCheck {
    // FIXME: do we need to clear these fields in the end?
    /**
     * Set of all nested classes.
     */
    private final Set<String> nested = new HashSet<>();

    /**
     * Whether we already visited root class of the .java file.
     */
    private boolean root;

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
            TokenTypes.CLASS_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.LITERAL_NEW,
        };
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
        if (ast.getType() == TokenTypes.CLASS_DEF
            || ast.getType() == TokenTypes.ENUM_DEF
            || ast.getType() == TokenTypes.INTERFACE_DEF) {
            this.scanForNestedClassesIfNecessary(ast);
        }
        if (ast.getType() == TokenTypes.LITERAL_NEW) {
            this.visitNewExpression(ast);
        }
    }

    /**
     * Checks if class to be instantiated is nested and unqualified.
     *
     * FIXME: currently only simple paths are detected
     * (i.e. `new Foo`, but not `new Foo.Bar`)
     * @param expr EXPR LITERAL_NEW node that needs to be checked
     */
    private void visitNewExpression(final DetailAST expr) {
        final DetailAST child = expr.getFirstChild();
        if (child.getType() == TokenTypes.IDENT) {
            if (this.nested.contains(child.getText())) {
                this.log(child, "Static inner class should be qualified with outer class");
            }
        } else if (child.getType() != TokenTypes.DOT) {
            final String message = String.format("unsupported input %d", child.getType());
            throw new IllegalStateException(message);
        }
    }

    /**
     * If provided class is top-level, scans it for nested classes.
     * FIXME: currently it assumes there can be only one top-level class
     *
     * @param node Class-like AST node
     */
    private void scanForNestedClassesIfNecessary(final DetailAST node) {
        if (!this.root) {
            this.root = true;
            this.scanClass(node);
        }
    }

    /**
     * Scans class for all nested sub-classes.
     *
     * FIXME: checkstyle discourages manual traversing of AST,
     * but exactly this is happening here.
     * @param node Class-like AST node that needs to be checked
     */
    private void scanClass(final DetailAST node) {
        this.nested.add(getClassName(node));
        final DetailAST content = node.findFirstToken(TokenTypes.OBJBLOCK);
        if (content == null) {
            return;
        }
        for (
            DetailAST child = content.getFirstChild();
            child != null;
            child  = child.getNextSibling()
        ) {
            if (child.getType() == TokenTypes.CLASS_DEF
                || child.getType() == TokenTypes.ENUM_DEF
                || child.getType() == TokenTypes.INTERFACE_DEF) {
                this.scanClass(child);
            }
        }
    }

    /**
     * Returns class name.
     * @param clazz Class-like AST node
     * @return Class name
     */
    private static String getClassName(final DetailAST clazz) {
        for (
            DetailAST child = clazz.getFirstChild();
            child != null;
            child = child.getNextSibling()
        ) {
            if (child.getType() == TokenTypes.IDENT) {
                return child.getText();
            }
        }
        throw new IllegalStateException("unexpected input: can not find class name");
    }
}
