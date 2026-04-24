/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that there is no empty line between a javadoc and it's subject,
 * and that no annotation is placed above the javadoc.
 *
 * <p>You can't have empty lines between javadoc block and
 * a class/method/variable. They should stay together, always.
 *
 * <p>Annotations must be placed after the javadoc, not before it,
 * so that the javadoc stays next to the subject it describes.
 *
 * @since 0.3
 */
public final class JavadocLocationCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.METHOD_DEF,
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
        if (!JavadocLocationCheck.isField(ast)) {
            return;
        }
        final String[] lines = this.getLines();
        this.checkEmptyLines(ast, lines);
        this.checkAnnotationAboveJavadoc(ast, lines);
    }

    /**
     * Check that there are no empty lines between the javadoc and the subject.
     * @param ast The AST node of the subject
     * @param lines The file lines
     */
    private void checkEmptyLines(final DetailAST ast, final String[] lines) {
        int current = ast.getLineNo();
        boolean found = false;
        --current;
        while (true) {
            if (current <= 0) {
                break;
            }
            final String line = lines[current - 1].trim();
            if (line.endsWith("*/")) {
                found = true;
                break;
            }
            if (!line.isEmpty()) {
                break;
            }
            --current;
        }
        if (found) {
            final int diff = ast.getLineNo() - current;
            if (diff > 1) {
                for (int pos = 1; pos < diff; pos += 1) {
                    this.log(
                        current + pos,
                        "Empty line between javadoc and subject"
                    );
                }
            }
        }
    }

    /**
     * Check that no annotation is placed above the javadoc of the subject.
     * @param ast The AST node of the subject
     * @param lines The file lines
     */
    private void checkAnnotationAboveJavadoc(
        final DetailAST ast, final String[] lines
    ) {
        final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers == null) {
            return;
        }
        final DetailAST after = modifiers.getNextSibling();
        if (after == null) {
            return;
        }
        final int signature = after.getLineNo();
        int annotation = Integer.MAX_VALUE;
        DetailAST child = modifiers.getFirstChild();
        while (child != null) {
            if (child.getType() == TokenTypes.ANNOTATION
                && child.getLineNo() < annotation) {
                annotation = child.getLineNo();
            }
            child = child.getNextSibling();
        }
        if (annotation == Integer.MAX_VALUE) {
            return;
        }
        for (int pos = annotation + 1; pos < signature; pos += 1) {
            final String line = lines[pos - 1].trim();
            if (line.startsWith("/**") || line.endsWith("*/")) {
                this.log(
                    annotation,
                    "Annotation must be placed after Javadoc"
                );
                break;
            }
        }
    }

    /**
     * Returns {@code TRUE} if a specified node is something that should have
     * a Javadoc, which includes classes, interface, class methods, and
     * class variables.
     * @param node Node to check
     * @return Is it a Javadoc-required entity?
     */
    private static boolean isField(final DetailAST node) {
        boolean yes = true;
        if (TokenTypes.VARIABLE_DEF == node.getType()) {
            yes = TokenTypes.OBJBLOCK == node.getParent().getType();
        }
        return yes;
    }
}
