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

    /**
     * Default constructor.
     */
    public JavadocLocationCheck() {
        // nothing to initialize
    }

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

    private static boolean isField(final DetailAST node) {
        boolean yes = true;
        if (TokenTypes.VARIABLE_DEF == node.getType()) {
            yes = TokenTypes.OBJBLOCK == node.getParent().getType();
        }
        return yes;
    }
}
