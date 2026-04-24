/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids nested {@code switch} statements.
 *
 * <p>A {@code switch} inside another {@code switch} hides flow of control
 * behind two levels of branching and almost always signals that the enclosing
 * method is doing too much. Extract the inner statement into its own method
 * instead. Mirrors Checkstyle's built-in {@code NestedIfDepth},
 * {@code NestedForDepth} and {@code NestedTryDepth} modules.</p>
 *
 * @since 0.24
 */
public final class NestedSwitchCheck extends AbstractCheck {

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
        return new int[] {TokenTypes.LITERAL_SWITCH};
    }

    @Override
    public void visitToken(final DetailAST ast) {
        DetailAST parent = ast.getParent();
        while (parent != null) {
            if (parent.getType() == TokenTypes.LITERAL_SWITCH) {
                this.log(ast.getLineNo(), "Nested switch statements are not allowed");
                break;
            }
            parent = parent.getParent();
        }
    }
}
