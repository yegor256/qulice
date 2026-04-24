/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids the {@code expected} parameter on JUnit's {@code @Test} annotation.
 *
 * <p>The JUnit 4 construct {@code @Test(expected = SomeException.class)}
 * is too coarse: the whole test body is inspected for the exception, so a
 * failure during set-up (e.g. an unexpected {@code NullPointerException})
 * silently satisfies the assertion. In addition, nothing can be checked
 * about the message or the cause of the thrown exception. Prefer
 * {@code Assertions.assertThrows(...)} (JUnit 5) or a Hamcrest-style
 * {@code try}/{@code catch} with explicit assertions instead. See
 * <a href="https://github.com/yegor256/qulice/issues/668">#668</a>.</p>
 *
 * @since 0.24
 */
public final class ProhibitTestExpectedCheck extends AbstractCheck {

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
        return new int[] {TokenTypes.ANNOTATION};
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (ProhibitTestExpectedCheck.isTest(ast)
            && ProhibitTestExpectedCheck.hasExpected(ast)) {
            this.log(
                ast.getLineNo(),
                "@Test(expected = ...) is not allowed, use Assertions.assertThrows() instead"
            );
        }
    }

    /**
     * Is this a JUnit {@code @Test} annotation (short or fully-qualified)?
     * @param ast The ANNOTATION node
     * @return True if its simple name is {@code Test}
     */
    private static boolean isTest(final DetailAST ast) {
        final DetailAST ident = ast.findFirstToken(TokenTypes.IDENT);
        final boolean match;
        if (ident == null) {
            final DetailAST dot = ast.findFirstToken(TokenTypes.DOT);
            match = dot != null
                && dot.getLastChild() != null
                && "Test".equals(dot.getLastChild().getText());
        } else {
            match = "Test".equals(ident.getText());
        }
        return match;
    }

    /**
     * Does this annotation have an {@code expected} member?
     * @param ast The ANNOTATION node
     * @return True if an {@code expected = ...} pair is present
     */
    private static boolean hasExpected(final DetailAST ast) {
        boolean found = false;
        DetailAST child = ast.getFirstChild();
        while (child != null) {
            if (child.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR) {
                final DetailAST name = child.findFirstToken(TokenTypes.IDENT);
                if (name != null && "expected".equals(name.getText())) {
                    found = true;
                    break;
                }
            }
            child = child.getNextSibling();
        }
        return found;
    }
}
