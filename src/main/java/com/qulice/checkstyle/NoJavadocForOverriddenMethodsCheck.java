/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

/**
 * Checks that there is no Javadoc for inherited methods.
 * Users may have a different understanding of your method
 * based on whether they examine the method in the supertype
 * or the subtype and it may cause confusion.
 *
 * @since 0.16
 */
public final class NoJavadocForOverriddenMethodsCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
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
    @SuppressWarnings("deprecation")
    public void visitToken(final DetailAST ast) {
        if (AnnotationUtil.containsAnnotation(ast, "Override")) {
            final FileContents contents = getFileContents();
            final TextBlock javadoc = contents.getJavadocBefore(
                ast.getLineNo()
            );
            if (javadoc != null) {
                log(ast, "Overridden methods should not have Javadoc");
            }
        }
    }
}
