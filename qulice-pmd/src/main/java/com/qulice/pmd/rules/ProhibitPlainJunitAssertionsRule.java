/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;

/**
 * Rule to check plain assertions in JUnit tests.
 * @since 0.17
 */
@SuppressWarnings("deprecation")
public final class ProhibitPlainJunitAssertionsRule
    extends net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule {

    /**
     * Mask of prohibited imports.
     */
    private static final String[] PROHIBITED = {
        "org.junit.Assert.assert",
        "junit.framework.Assert.assert",
    };

    @Override
    public Object visit(final ASTMethodDeclaration method, final Object data) {
        if (this.isJUnitMethod(method, data)
            && this.containsPlainJunitAssert(method.getBody())) {
            this.asCtx(data).addViolation(method);
        }
        return data;
    }

    @Override
    public Object visit(final ASTImportDeclaration imp, final Object data) {
        for (final String element : ProhibitPlainJunitAssertionsRule
            .PROHIBITED) {
            if (imp.getImportedName().contains(element)) {
                this.asCtx(data).addViolation(imp);
                break;
            }
        }
        return super.visit(imp, data);
    }

    /**
     * Recursively verifies if node contains plain JUnit assert statements.
     * @param node Root statement node to search
     * @return True if statement contains plain JUnit assertions, false
     *  otherwise
     */
    private boolean containsPlainJunitAssert(final Node node) {
        boolean found = false;
        if (node instanceof ASTStatementExpression
            && ProhibitPlainJunitAssertionsRule.isPlainJunitAssert(node)) {
            found = true;
        }
        if (!found) {
            for (int iter = 0; iter < node.jjtGetNumChildren(); iter += 1) {
                final Node child = node.jjtGetChild(iter);
                if (this.containsPlainJunitAssert(child)) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Tells if the statement is an assert statement or not.
     * @param statement Root node to search assert statements
     * @return True is statement is assert, false otherwise
     */
    private static boolean isPlainJunitAssert(final Node statement) {
        final ASTPrimaryExpression expression =
            ProhibitPlainJunitAssertionsRule.getChildNodeWithType(
                statement, ASTPrimaryExpression.class
            );
        final ASTPrimaryPrefix prefix =
            ProhibitPlainJunitAssertionsRule.getChildNodeWithType(
                expression, ASTPrimaryPrefix.class
            );
        final ASTName name = ProhibitPlainJunitAssertionsRule
            .getChildNodeWithType(prefix, ASTName.class);
        boolean assrt = false;
        if (name != null) {
            final String img = name.getImage();
            assrt = img != null && (img.startsWith("assert")
                || img.startsWith("Assert.assert"));
        }
        return assrt;
    }

    /**
     * Gets child node with specified type.
     * @param node Parent node
     * @param clazz Specified class
     * @param <T> Node type
     * @return Child node if exists, null otherwise
     */
    private static <T extends Node> T getChildNodeWithType(final Node node,
        final Class<T> clazz) {
        T expression = null;
        if (node != null && node.jjtGetNumChildren() > 0
            && clazz.isInstance(node.jjtGetChild(0))) {
            expression = clazz.cast(node.jjtGetChild(0));
        }
        return expression;
    }

}
