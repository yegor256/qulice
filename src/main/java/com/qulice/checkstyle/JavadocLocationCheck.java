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
    private void checkEmptyLines(final DetailAST ast, final String... lines) {
        final int current = JavadocLocationCheck.javadocEnd(
            ast.getLineNo() - 1, lines
        );
        if (current > 0) {
            final int diff = ast.getLineNo() - current;
            for (int pos = 1; pos < diff; pos += 1) {
                this.log(
                    current + pos,
                    "Empty line between javadoc and subject"
                );
            }
        }
    }

    /**
     * Walks upward from the given line and returns the line number of
     * the closing javadoc marker if only blank lines separate it from
     * the subject, otherwise zero.
     * @param from Line just above the subject (one-based)
     * @param lines The file lines
     * @return Line number of the javadoc end, or zero if none
     */
    private static int javadocEnd(final int from, final String... lines) {
        int current = from;
        int result = 0;
        while (current > 0) {
            final String line = lines[current - 1].trim();
            if (line.endsWith("*/")) {
                result = current;
                break;
            }
            if (!line.isEmpty()) {
                break;
            }
            current -= 1;
        }
        return result;
    }

    /**
     * Check that no annotation is placed above the javadoc of the subject.
     * @param ast The AST node of the subject
     * @param lines The file lines
     */
    private void checkAnnotationAboveJavadoc(
        final DetailAST ast, final String... lines
    ) {
        final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null) {
            final DetailAST after = modifiers.getNextSibling();
            final int annotation = JavadocLocationCheck.firstAnnotationLine(
                modifiers
            );
            if (after != null && annotation != Integer.MAX_VALUE
                && JavadocLocationCheck.javadocBetween(
                    annotation, after.getLineNo(), lines
                )) {
                this.log(annotation, "Annotation must be placed after Javadoc");
            }
        }
    }

    /**
     * Returns the line number of the first annotation under the given
     * modifiers node, or {@link Integer#MAX_VALUE} when there is none.
     * @param modifiers The MODIFIERS token
     * @return Line number of the first annotation
     */
    private static int firstAnnotationLine(final DetailAST modifiers) {
        int line = Integer.MAX_VALUE;
        DetailAST child = modifiers.getFirstChild();
        while (child != null) {
            if (child.getType() == TokenTypes.ANNOTATION
                && child.getLineNo() < line) {
                line = child.getLineNo();
            }
            child = child.getNextSibling();
        }
        return line;
    }

    /**
     * Tells whether any javadoc opening or closing marker appears
     * between the given lines (exclusive of the start, exclusive of the
     * end).
     * @param start Lower bound line, exclusive
     * @param end Upper bound line, exclusive
     * @param lines The file lines
     * @return True when a javadoc marker is found in the range
     */
    private static boolean javadocBetween(final int start, final int end,
        final String... lines) {
        boolean found = false;
        for (int pos = start + 1; pos < end; pos += 1) {
            final String line = lines[pos - 1].trim();
            if (line.startsWith("/**") || line.endsWith("*/")) {
                found = true;
                break;
            }
        }
        return found;
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
