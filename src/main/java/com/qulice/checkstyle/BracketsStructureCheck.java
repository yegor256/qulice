/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks node/closing brackets to be the last symbols on the line.
 *
 * <p>This is how a correct bracket structure should look like:
 *
 * <pre>
 * String text = String.format(
 *   "some text: %s",
 *   new Foo().with(
 *     "abc",
 *     "foo"
 *   )
 * );
 * </pre>
 *
 * <p>The motivation for such formatting is simple - we want to see the entire
 * block as fast as possible. When you look at a block of code you should be
 * able to see where it starts and where it ends. In exactly the same way
 * we organize curled brackets.
 *
 * <p>In other words, when you open a bracket and can't close it at the same
 * line - you should leave it as the last symbol at this line.
 *
 * @since 0.3
 */
public final class BracketsStructureCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_NEW,
            TokenTypes.METHOD_CALL,
            TokenTypes.RESOURCE_SPECIFICATION,
            TokenTypes.ANNOTATION,
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
        if (ast.getType() == TokenTypes.RESOURCE_SPECIFICATION) {
            this.checkResources(ast);
        } else if (ast.getType() == TokenTypes.ANNOTATION) {
            this.checkAnnotation(ast);
        } else if (ast.getType() == TokenTypes.METHOD_CALL
            || ast.getType() == TokenTypes.LITERAL_NEW) {
            this.checkParams(ast);
        } else {
            final DetailAST brackets = ast.findFirstToken(TokenTypes.LPAREN);
            if (brackets != null) {
                this.checkParams(brackets);
            }
        }
    }

    /**
     * Checks params statement to satisfy the rule.
     * @param node Tree node, containing method call statement
     */
    private void checkParams(final DetailAST node) {
        final DetailAST closing = node.findFirstToken(TokenTypes.RPAREN);
        if (closing != null) {
            this.checkLines(node, node.getLineNo(), closing.getLineNo());
        }
    }

    /**
     * Checks params statement to satisfy the rule.
     * @param node Tree node, containing method call statement
     * @param start First line
     * @param end Final line
     */
    private void checkLines(final DetailAST node, final int start,
        final int end) {
        if (start != end) {
            final DetailAST elist = node.findFirstToken(TokenTypes.ELIST);
            final int pline = elist.getLineNo();
            if (pline == start) {
                this.log(pline, "Parameters should start on a new line");
            }
            this.checkExpressionList(elist, end);
        }
    }

    /**
     * Checks expression list if closing bracket is on new line.
     * @param elist Tree node, containing expression list
     * @param end Final line
     */
    private void checkExpressionList(final DetailAST elist, final int end) {
        if (elist.getChildCount() > 0) {
            DetailAST last = elist.getLastChild();
            while (last.getChildCount() > 0) {
                last = last.getLastChild();
            }
            final int lline = last.getLineNo();
            if (lline == end) {
                this.log(lline, "Closing bracket should be on a new line");
            }
        }
    }

    /**
     * Checks annotation with multi-line parameter list.
     * @param node Tree node, containing the ANNOTATION
     */
    private void checkAnnotation(final DetailAST node) {
        final DetailAST opening = node.findFirstToken(TokenTypes.LPAREN);
        final DetailAST closing = node.findFirstToken(TokenTypes.RPAREN);
        if (opening != null && closing != null
            && opening.getLineNo() != closing.getLineNo()) {
            DetailAST first = opening.getNextSibling();
            DetailAST last = closing.getPreviousSibling();
            DetailAST start = opening;
            DetailAST end = closing;
            if (first != null && first == last
                && first.getType() == TokenTypes.ANNOTATION_ARRAY_INIT) {
                final DetailAST rcurly = first.getLastChild();
                if (rcurly != null
                    && rcurly.getType() == TokenTypes.RCURLY) {
                    start = first;
                    end = rcurly;
                    first = first.getFirstChild();
                    if (first == rcurly) {
                        first = null;
                    }
                    last = rcurly.getPreviousSibling();
                }
            }
            if (start.getLineNo() != end.getLineNo()) {
                this.checkAnnotationBounds(first, last, start, end);
            }
        }
    }

    /**
     * Logs violations at the first/last content of a multi-line annotation.
     * @param first First content token after the opening bracket
     * @param last Last content token before the closing bracket
     * @param start Effective opening bracket token
     * @param end Effective closing bracket token
     */
    private void checkAnnotationBounds(final DetailAST first,
        final DetailAST last, final DetailAST start, final DetailAST end) {
        if (first != null
            && first.getLineNo() == start.getLineNo()) {
            this.log(
                first.getLineNo(),
                "Parameters should start on a new line"
            );
        }
        DetailAST leaf = last;
        while (leaf != null && leaf.getChildCount() > 0) {
            leaf = leaf.getLastChild();
        }
        if (leaf != null
            && leaf.getLineNo() == end.getLineNo()) {
            this.log(
                leaf.getLineNo(),
                "Closing bracket should be on a new line"
            );
        }
    }

    /**
     * Checks resources of try-with-resources statement.
     * @param node Tree node, containing the RESOURCE_SPECIFICATION
     */
    private void checkResources(final DetailAST node) {
        final DetailAST opening = node.findFirstToken(TokenTypes.LPAREN);
        final DetailAST closing = node.findFirstToken(TokenTypes.RPAREN);
        if (opening != null && closing != null
            && opening.getLineNo() != closing.getLineNo()) {
            this.checkResourceBody(node, opening, closing);
        }
    }

    /**
     * Checks RESOURCES body inside a multiline try-with-resources.
     * @param node Tree node with the RESOURCE_SPECIFICATION
     * @param opening The opening LPAREN token
     * @param closing The closing RPAREN token
     */
    private void checkResourceBody(final DetailAST node,
        final DetailAST opening, final DetailAST closing) {
        final DetailAST resources = node.findFirstToken(TokenTypes.RESOURCES);
        if (resources != null) {
            if (resources.getLineNo() == opening.getLineNo()) {
                this.log(
                    resources.getLineNo(),
                    "Parameters should start on a new line"
                );
            }
            DetailAST last = resources.getLastChild();
            while (last != null && last.getChildCount() > 0) {
                last = last.getLastChild();
            }
            if (last != null && last.getLineNo() == closing.getLineNo()) {
                this.log(
                    last.getLineNo(),
                    "Closing bracket should be on a new line"
                );
            }
        }
    }
}
