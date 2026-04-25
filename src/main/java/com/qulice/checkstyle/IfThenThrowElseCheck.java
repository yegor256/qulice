/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids an {@code else} branch when the {@code then} branch of an
 * {@code if} statement ends with a {@code throw}.
 *
 * <p>When the {@code then} branch unconditionally throws an exception
 * the control flow never reaches the code that follows the
 * {@code if}/{@code else} pair, so the {@code else} keyword adds no
 * information and only deepens nesting. Remove the {@code else} and
 * leave the alternative body at the original indentation level:</p>
 *
 * <pre>
 * // wrong
 * if (x &lt; 0) {
 *     throw new IllegalArgumentException("negative");
 * } else {
 *     process(x);
 * }
 * // right
 * if (x &lt; 0) {
 *     throw new IllegalArgumentException("negative");
 * }
 * process(x);
 * </pre>
 *
 * <p>See <a href="https://www.yegor256.com/2015/01/21/if-then-throw-else.html">
 * "If-Then-Throw-Else"</a> for the rationale.</p>
 *
 * @since 0.24
 */
public final class IfThenThrowElseCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return this.getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {TokenTypes.LITERAL_IF};
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final DetailAST branch = ast.findFirstToken(TokenTypes.LITERAL_ELSE);
        if (branch != null
            && IfThenThrowElseCheck.alwaysThrows(IfThenThrowElseCheck.thenBranch(ast))) {
            this.log(
                ast.getLineNo(),
                "Avoid ''else'' when ''then'' branch ends with ''throw''"
            );
        }
    }

    /**
     * Locates the statement or block that forms the {@code then} branch
     * of the given {@code if}.
     * @param ast The {@code LITERAL_IF} node
     * @return The first statement inside the {@code then} branch
     */
    private static DetailAST thenBranch(final DetailAST ast) {
        final DetailAST rparen = ast.findFirstToken(TokenTypes.RPAREN);
        DetailAST result = null;
        if (rparen != null) {
            result = rparen.getNextSibling();
        }
        return result;
    }

    /**
     * Tells whether control flow exits the given node through an
     * unconditional {@code throw}.
     * @param node The node to inspect
     * @return {@code true} when the last statement is {@code throw}
     */
    private static boolean alwaysThrows(final DetailAST node) {
        final boolean result;
        if (node == null) {
            result = false;
        } else if (node.getType() == TokenTypes.LITERAL_THROW) {
            result = true;
        } else if (node.getType() == TokenTypes.SLIST) {
            result = IfThenThrowElseCheck.endsWithThrow(node);
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Checks whether the last meaningful statement inside a
     * {@code SLIST} is a {@code throw}.
     * @param slist The {@code SLIST} node
     * @return {@code true} when the last statement is {@code throw}
     */
    private static boolean endsWithThrow(final DetailAST slist) {
        DetailAST last = slist.getLastChild();
        while (last != null && last.getType() == TokenTypes.RCURLY) {
            last = last.getPreviousSibling();
        }
        return last != null && last.getType() == TokenTypes.LITERAL_THROW;
    }
}
