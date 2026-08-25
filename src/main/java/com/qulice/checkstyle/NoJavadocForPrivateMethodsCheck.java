/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that there is no Javadoc for private methods, both static
 * and non-static ones.
 *
 * <p>A private method is an implementation detail of its own class and
 * has no users outside of it. Its name, the names of its parameters and
 * its body say everything a reader needs, while a Javadoc block above it
 * only repeats them and then rots, staying behind after the method is
 * renamed or its contract changes.</p>
 *
 * <p>Private constructors are not affected, since a constructor is not
 * a method and its Javadoc often carries the only explanation of why
 * the class must not be instantiated.</p>
 *
 * @since 0.73.4
 */
public final class NoJavadocForPrivateMethodsCheck extends AbstractCheck {

    /**
     * Default constructor.
     */
    public NoJavadocForPrivateMethodsCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.METHOD_DEF};
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
        final boolean prv = ast.findFirstToken(TokenTypes.MODIFIERS)
            .findFirstToken(TokenTypes.LITERAL_PRIVATE) != null;
        if (prv && this.getFileContents().getJavadocBefore(ast.getLineNo()) != null) {
            final DetailAST name = ast.findFirstToken(TokenTypes.IDENT);
            this.log(
                name,
                String.format(
                    "Private method \"%s\" must not have Javadoc",
                    name.getText()
                )
            );
        }
    }
}
