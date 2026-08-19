/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks for not using concatenation of string literals in any form.
 *
 * <p>The following constructs are prohibited:
 *
 * <pre>
 * String a = "done in " + time + " seconds";
 * System.out.println("File not found: " + file);
 * x += "done";
 * </pre>
 *
 * <p>You should avoid string concatenation at all cost. Why? There are two
 * reasons: readability of the code and translateability. First of all it's
 * difficult to understand how the text will look after concatenation,
 * especially if the text is long and there are more than a few {@code +}
 * operators. Second, you won't be able to translate your text to other
 * languages later, if you don't have solid string literals.
 *
 * <p>There are two alternatives to concatenation: {@link StringBuilder}
 * and {@link String#format(String,Object[])}.
 *
 * @since 0.3
 */
public final class StringLiteralsConcatenationCheck extends AbstractCheck {

    /**
     * Default constructor.
     */
    public StringLiteralsConcatenationCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.OBJBLOCK};
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
        final List<DetailAST> pluses = this.findChildAstsOfType(
            ast,
            TokenTypes.PLUS,
            TokenTypes.PLUS_ASSIGN
        );
        for (final DetailAST plus : pluses) {
            if (this.hasStringLiteralOperand(plus)) {
                this.log(plus, "Concatenation of string literals prohibited");
            }
        }
    }

    private List<DetailAST> findChildAstsOfType(final DetailAST tree,
        final int... types) {
        final List<DetailAST> children = new ArrayList<>(0);
        DetailAST child = tree.getFirstChild();
        while (child != null) {
            if (StringLiteralsConcatenationCheck.isOfType(child, types)) {
                children.add(child);
            } else {
                children.addAll(this.findChildAstsOfType(child, types));
            }
            child = child.getNextSibling();
        }
        return children;
    }

    private boolean hasStringLiteralOperand(final DetailAST node) {
        boolean found = false;
        DetailAST child = node.getFirstChild();
        while (child != null) {
            final int type = child.getType();
            if (type == TokenTypes.STRING_LITERAL) {
                found = true;
                break;
            }
            if ((type == TokenTypes.PLUS || type == TokenTypes.PLUS_ASSIGN)
                && this.hasStringLiteralOperand(child)) {
                found = true;
                break;
            }
            child = child.getNextSibling();
        }
        return found;
    }

    private static boolean isOfType(final DetailAST ast, final int... types) {
        boolean yes = false;
        for (final int type : types) {
            if (ast.getType() == type) {
                yes = true;
                break;
            }
        }
        return yes;
    }
}
