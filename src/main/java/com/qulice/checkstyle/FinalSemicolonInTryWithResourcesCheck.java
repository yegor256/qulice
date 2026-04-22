/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that try-with-resources does not end with a semicolon. Implementation
 * relies on existence of semicolon inside of RESOURCE_SPECIFICATION token
 * as interpreted by Checkstyle.
 *
 * @since 0.15
 */
public final class FinalSemicolonInTryWithResourcesCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
            TokenTypes.RESOURCE_SPECIFICATION,
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
        final int semicolons = ast.getChildCount(TokenTypes.SEMI);
        if (semicolons > 0) {
            this.log(
                ast.getLineNo(),
                "Extra semicolon in the end of try-with-resources head."
            );
        }
    }
}
