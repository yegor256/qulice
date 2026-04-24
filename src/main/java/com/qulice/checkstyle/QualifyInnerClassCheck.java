/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
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
 */
public final class QualifyInnerClassCheck extends AbstractCheck {

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
    public void beginTree(final DetailAST ast) {
        this.nested.clear();
        this.root = false;
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
     * @param expr EXPR LITERAL_NEW node that needs to be checked
     */
    private void visitNewExpression(final DetailAST expr) {
        final DetailAST child = expr.getFirstChild();
        if (child != null
            && child.getType() == TokenTypes.IDENT
            && this.nested.contains(child.getText())) {
            this.log(child, "Static inner class should be qualified with outer class");
        }
    }

    /**
     * If provided class is top-level, scans it for nested classes.
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
            child = child.getNextSibling()
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
        throw new IllegalStateException("Unable to find class name");
    }
}
