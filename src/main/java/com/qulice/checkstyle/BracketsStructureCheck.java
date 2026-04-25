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
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods"})
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
            final int pline = BracketsStructureCheck.firstParamLine(elist);
            if (pline == start) {
                this.log(start, "Parameters should start on a new line");
            }
            this.checkExpressionList(elist, end);
        }
    }

    /**
     * Returns the line number of the first actual token inside the
     * expression list. ELIST itself reports {@code lparen + 1} regardless
     * of where the first parameter actually is, so we descend to the
     * leftmost leaf to find the real position.
     * @param elist Tree node, containing the expression list
     * @return Line number of the first parameter token
     */
    private static int firstParamLine(final DetailAST elist) {
        DetailAST leaf = elist;
        while (leaf.getFirstChild() != null) {
            leaf = leaf.getFirstChild();
        }
        final int line;
        if (leaf.equals(elist)) {
            line = elist.getLineNo();
        } else {
            line = leaf.getLineNo();
        }
        return line;
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
            final DetailAST first = opening.getNextSibling();
            final DetailAST last = closing.getPreviousSibling();
            final DetailAST rcurly =
                BracketsStructureCheck.arrayInitRcurly(first, last);
            if (rcurly == null) {
                this.checkBoundsStart(first, opening);
                this.checkBoundsEnd(last, closing);
            } else if (first.getLineNo() != rcurly.getLineNo()) {
                this.checkArrayBounds(first, rcurly);
            }
        }
    }

    /**
     * Returns the closing curly of an annotation array initializer when
     * the annotation contains exactly one such child, otherwise null.
     * @param first First child after the LPAREN
     * @param last Last child before the RPAREN
     * @return RCURLY token or null
     */
    private static DetailAST arrayInitRcurly(final DetailAST first,
        final DetailAST last) {
        DetailAST rcurly = null;
        if (first != null && first.equals(last)
            && first.getType() == TokenTypes.ANNOTATION_ARRAY_INIT) {
            final DetailAST candidate = first.getLastChild();
            if (candidate != null
                && candidate.getType() == TokenTypes.RCURLY) {
                rcurly = candidate;
            }
        }
        return rcurly;
    }

    /**
     * Logs at the first/last content of a multi-line annotation array
     * initializer. The empty initializer {@code {}} has no content
     * before the closing curly, so only the end is checked.
     * @param array The ANNOTATION_ARRAY_INIT token
     * @param rcurly The closing RCURLY of that initializer
     */
    private void checkArrayBounds(final DetailAST array,
        final DetailAST rcurly) {
        final DetailAST inner = array.getFirstChild();
        if (!inner.equals(rcurly)) {
            this.checkBoundsStart(inner, array);
        }
        this.checkBoundsEnd(rcurly.getPreviousSibling(), rcurly);
    }

    /**
     * Logs a violation when the first content sits on the same line as
     * the opening bracket.
     * @param first First content token (may be null)
     * @param start Opening bracket token
     */
    private void checkBoundsStart(final DetailAST first,
        final DetailAST start) {
        if (first != null && first.getLineNo() == start.getLineNo()) {
            this.log(
                first.getLineNo(),
                "Parameters should start on a new line"
            );
        }
    }

    /**
     * Logs a violation when the last content sits on the same line as
     * the closing bracket.
     * @param last Last content token (may be null)
     * @param end Closing bracket token
     */
    private void checkBoundsEnd(final DetailAST last, final DetailAST end) {
        DetailAST leaf = last;
        while (leaf != null && leaf.getChildCount() > 0) {
            leaf = leaf.getLastChild();
        }
        if (leaf != null && leaf.getLineNo() == end.getLineNo()) {
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
