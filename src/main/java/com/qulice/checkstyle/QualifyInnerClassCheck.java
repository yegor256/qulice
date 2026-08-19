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
 * @since 0.18
 */
public final class QualifyInnerClassCheck extends AbstractCheck {

    /**
     * Set of all nested classes.
     */
    private final Set<String> nested;

    /**
     * Whether we already visited root class of the .java file.
     */
    private boolean root;

    /**
     * Default constructor.
     */
    public QualifyInnerClassCheck() {
        this.nested = new HashSet<>();
    }

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

    private void visitNewExpression(final DetailAST expr) {
        final DetailAST child = expr.getFirstChild();
        if (child != null
            && child.getType() == TokenTypes.IDENT
            && this.nested.contains(child.getText())) {
            this.log(child, "Static inner class should be qualified with outer class");
        }
    }

    private void scanForNestedClassesIfNecessary(final DetailAST node) {
        if (!this.root) {
            this.root = true;
            this.scanClass(node);
        }
    }

    private void scanClass(final DetailAST node) {
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
                this.nested.add(getClassName(child));
                this.scanClass(child);
            }
        }
    }

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
